# Copyright (c) 2017-2018 Robert Hodges.  All rights reserved. 

import boto3
import botocore
import json
from pprint import pformat

from goldfin_ocr.json_xlate import json_dict_to_model, model_to_json_dict
import goldfin_ocr.util as util

# Our logger.
import logging

logger = logging.getLogger(__name__)


class StructuredMessage(object):
    """Implements a message with headers and content
    """

    def __init__(self, operation=None, type=None, xact_tag=None,
                 tenant_id=None, content_locator=None, content=None,
                 content_class=None, receipt_handle=None):
        """Initialize message contents

        :param operation: Operation, e.g., "DocumentScan"
        :param type: Message type (enum values "request" or "response" )
        :param xact_tag: Transaction tag
        :param tenant_id: ID of tenant for whom transaction is occurring
        :param content_locator: S3 object storage locator for large messages
        :param content: Inline message content as string
        :param content_class: Content class name for deserialization
        :param receipt_handle: SQS ID used to delete an incoming message
        """
        self.operation = operation
        self.type = type
        self.xact_tag = xact_tag
        self.tenant_id = tenant_id
        self.content = content
        self.content_locator = content_locator
        self.content_class = content_class
        self.receipt_handle = receipt_handle

    def encode(self, object):
        """Encode Swagger object to JSON string"""
        return model_to_json_dict(object)

    def decode(self, klass):
        """Decode Swagger object from JSON string"""
        return json_dict_to_model(self.content, klass)

    def to_str(self):
        """Return string representation"""
        return pformat(self.__dict__)

    def __repr__(self):
        """Printable representation for `print` and `pprint`"""
        return self.to_str()

    def __eq__(self, other):
        """Test equality with another message"""
        if not isinstance(other, StructuredMessage):
            return False
        return self.__dict__ == other.__dict__


class SqsConnection(object):
    """Implements a server that handles messages in a single SQS queue.

    See Javadoc comments in io.goldfin.shared.cloud.QueueConnection for 
    a description of the message transmission protocol. 
    """

    def __init__(self, queue_handle, group=None, access_key_id=None, 
                 secret_access_key=None,
                 region=None):
        """Initialize connection

        :param queue_handle: Unique queue name within the service. 
        :type name: str
        :param group: Service group name
        :type group: str
        :param access_key_id: AWS access key
        :type access_key_id: str
        :param secret_access_key: AWS secret key
        :type secret_access_key: str
        :param region: Client region
        :type region: str
        """
        self._name = "{0}-{1}".format(group, queue_handle)
        self._access_key_id = access_key_id
        self._secret_access_key = secret_access_key
        self._region = region

        self._sqs = boto3.resource('sqs',
                                   aws_access_key_id=self._access_key_id,
                                   aws_secret_access_key=self._secret_access_key,
                                   region_name=self._region)

    # Experimental support for Python context management (i.e., 'with'
    # syntax.
    def __enter__(self):
        """Return self for context management"""
        logger.debug("Entered SQS Connection")
        return self

    def __exit__(self, exc_type, exc_val, exc_tb):
        """Try to close boto connection"""
        try:
            self._sqs.meta.client._endpoint.http_session.close()
            logger.debug("Exited SQS connection")
        except Exception:
            logger.warning("Unable to close boto endpoint")

    def queueCreate(self):
        """Create the queue.  Has no effect if the queue already exists."""
        queue = self._sqs.create_queue(QueueName=self._name)
        logger.info("Created queue: {0}".format(queue.url))

    def queueExists(self):
        """Return True if queue exists"""
        return self._get_queue_safely() is not None

    def queueDelete(self):
        """Delete the queue.  Has no effect if queue does not exist."""
        queue = self._get_queue_safely()
        if queue is not None:
            logger.info("Deleting queue: {0}".format(queue.url))
            queue.delete()

    def _get_queue_safely(self):
        """Gets the queue or returns None if the queue does not exist"""
        try:
            return self._sqs.get_queue_by_name(QueueName=self._name)
        except botocore.exceptions.ClientError as e:
            # If a client error is thrown, we trace down to the SQS
            # error code.
            error_code = e.response['Error']['Code']
            if error_code == 'AWS.SimpleQueueService.NonExistentQueue':
                return None
            else:
                raise e

    def send(self, message):
        """Send a message

        :param message: A message containing content and descriptive headers
        :type message: StructuredMessage
        """
        queue = self._sqs.get_queue_by_name(QueueName=self._name)

        # Load attributes to dictionary and convert to SQS attribute format.
        metadata = {
            'operation': message.operation,
            'type': message.type,
            'xact_tag': message.xact_tag,
            'tenant_id': message.tenant_id,
            'content_class': message.content_class
        }
        attributes = self._to_message_attributes(metadata)

        # Convert the message to gzipped UTF bytes and add to attributes. 
        content_bytes = util.string_to_gzipped_utf8(message.content)
        attributes['_content'] = {
            'DataType': 'Binary', 'BinaryValue': content_bytes
        }

        # Prepare a message body that describes the content.
        body = json.dumps({
            "transmissionFormat": "UTF8BYTES;GZIP",
			"contentLocation": "HEADER"
        })

        # Send the content and attributes as an SQS message.
        logger.info(
            "Sending to SQS: queue={0}, content_length={1}".format(
            self._name, len(content_bytes)))
        queue.send_message(MessageBody=body, MessageAttributes=attributes)

    def receive(self):
        """Receive a message encoded as JSON

        :returns: StructuredMessage
        """
        queue = self._sqs.get_queue_by_name(QueueName=self._name)
        for message in queue.receive_messages(
                MessageAttributeNames=['All'], AttributeNames=['All']):

            # Fetch content from the '_content' message attribute.
            logger.debug("Message body: {0}".format(message.body))
            content_attribute = message.message_attributes.pop('_content')
            content_bytes = content_attribute['BinaryValue']
            body = util.gzipped_utf8_to_string(content_bytes)
            
            # Convert remaining attributes to dictionary.
            metadata = self._from_message_attributes(message.message_attributes)

            # Return a message response.
            return StructuredMessage(content=body,
                                     operation=metadata.get('operation'),
                                     type=metadata.get('type'),
                                     xact_tag=metadata.get('xact_tag'),
                                     tenant_id=metadata.get('tenant_id'),
                                     content_class=metadata.get(
                                         'content_class'),
                                     receipt_handle=message.receipt_handle)

        # If we got here there's nothing to see.
        return None

    def delete(self, message=None, receipt_handle=None):
        """
        Delete a message using either StructuredMessage object or the 
        receipt_handle value.
        """
        if message is not None:
            receipt_handle = message.receipt_handle
        queue = self._sqs.get_queue_by_name(QueueName=self._name)
        delete_response = queue.delete_messages(
            Entries = [
                {'Id': "1", 'ReceiptHandle': receipt_handle}
            ]
        )
        logger.debug("Deleting message: {0}".format(delete_response))


    def _to_message_attributes(self, metadata):
        """Convert dictionary to SQS message attributes"""
        attributes = {}
        for k, v in metadata.items():
            if v is None:
                pass
            elif type(v) == str:
                attributes[k] = {'DataType': 'String', 'StringValue': v}
            elif type(v) == int:
                attributes[k] = {'DataType': 'Number', 'StringValue': str(v)}
            else:
                raise Exception(
                    "Message attributes must be string or number type: key={0}, value={1}".format(
                        k, v))
        return attributes


    def _from_message_attributes(self, attributes):
        """Convert SQS message attributes to Python dictionary"""
        metadata = {}
        for k, v in attributes.items():
            if v['DataType'] == "String":
                metadata[k] = v['StringValue']
            elif v['DataType'] == "Number":
                metadata[k] = int(v['StringValue'])
            else:
                raise Exception(
                    "Message attributes must be string or number type: key={0}, value={1}".format(
                        k, v['DataType']))
        return metadata
