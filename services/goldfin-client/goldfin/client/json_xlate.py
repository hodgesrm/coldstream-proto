# Copyright (c) 2018 Robert Hodges.  All rights reserved. 

import datetime
import json
from six import iteritems


def json_dict_to_model(data, klass):
    """
    Deserializes raw JSON data in dict form to model class by translating property names.

    :param data: dict
    :type data: dict
    :param klass: class literal.
    :return: model object.
    """
    # Test to find out if this is a model class with swagger attribute names.
    instance = klass()
    if not instance.attribute_map:
        return data

    # Invert the generated name and translate names. 
    translated_data = {}
    for python_name, json_name in instance.attribute_map.items():
        translated_data[python_name] = data.get(json_name)
    return klass(**translated_data)


def model_to_json_dict(instance):
    """
    Serialize model class to dict with Swagger names.

    :param instance: model class instance
    :type instance: model object
    :return: dict.
    """
    # Test to ensure class has swagger attribute names.
    if not instance.attribute_map:
        raise Exception(
            "Instance is not a model class: {0}".format(type(instance)))
    else:
        return instance.to_dict()


class SwaggerJsonEncoder(json.JSONEncoder):
    include_nulls = False

    def default(self, o):
        # Look for o with swagger types and attribute map.
        if hasattr(o, 'swagger_types'):
            dikt = {}
            for attr, _ in iteritems(o.swagger_types):
                value = getattr(o, attr)
                if value is None and not self.include_nulls:
                    continue
                attr = o.attribute_map[attr]
                # Handle dates by converting to ISO format.
                if isinstance(value, datetime.datetime):
                    value = value.isoformat()
                dikt[attr] = value
            return dikt
        return json.JSONEncoder.default(self, o)
