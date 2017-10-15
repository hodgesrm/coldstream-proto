# coding: utf-8

"""
    Goldfin Invoice Processing API

    Goldfin Invoice Analysis

    OpenAPI spec version: 1.0.0
    Contact: rhodges@skylineresearch.comm
    Generated by: https://github.com/swagger-api/swagger-codegen.git
"""


from pprint import pformat
from six import iteritems
import re


class DocumentRegion(object):
    """
    NOTE: This class is auto generated by the swagger code generator program.
    Do not edit the class manually.
    """


    """
    Attributes:
      swagger_types (dict): The key is attribute name
                            and the value is attribute type.
      attribute_map (dict): The key is attribute name
                            and the value is json key in definition.
    """
    swagger_types = {
        'page': 'int',
        'left': 'int',
        'top': 'int',
        'right': 'int',
        'bottom': 'int'
    }

    attribute_map = {
        'page': 'page',
        'left': 'left',
        'top': 'top',
        'right': 'right',
        'bottom': 'bottom'
    }

    def __init__(self, page=None, left=None, top=None, right=None, bottom=None):
        """
        DocumentRegion - a model defined in Swagger
        """

        self._page = None
        self._left = None
        self._top = None
        self._right = None
        self._bottom = None

        if page is not None:
          self.page = page
        if left is not None:
          self.left = left
        if top is not None:
          self.top = top
        if right is not None:
          self.right = right
        if bottom is not None:
          self.bottom = bottom

    @property
    def page(self):
        """
        Gets the page of this DocumentRegion.
        Page number

        :return: The page of this DocumentRegion.
        :rtype: int
        """
        return self._page

    @page.setter
    def page(self, page):
        """
        Sets the page of this DocumentRegion.
        Page number

        :param page: The page of this DocumentRegion.
        :type: int
        """

        self._page = page

    @property
    def left(self):
        """
        Gets the left of this DocumentRegion.
        Left pixel coordinate

        :return: The left of this DocumentRegion.
        :rtype: int
        """
        return self._left

    @left.setter
    def left(self, left):
        """
        Sets the left of this DocumentRegion.
        Left pixel coordinate

        :param left: The left of this DocumentRegion.
        :type: int
        """

        self._left = left

    @property
    def top(self):
        """
        Gets the top of this DocumentRegion.
        Top pixel coordinate

        :return: The top of this DocumentRegion.
        :rtype: int
        """
        return self._top

    @top.setter
    def top(self, top):
        """
        Sets the top of this DocumentRegion.
        Top pixel coordinate

        :param top: The top of this DocumentRegion.
        :type: int
        """

        self._top = top

    @property
    def right(self):
        """
        Gets the right of this DocumentRegion.
        Right pixel coordinate

        :return: The right of this DocumentRegion.
        :rtype: int
        """
        return self._right

    @right.setter
    def right(self, right):
        """
        Sets the right of this DocumentRegion.
        Right pixel coordinate

        :param right: The right of this DocumentRegion.
        :type: int
        """

        self._right = right

    @property
    def bottom(self):
        """
        Gets the bottom of this DocumentRegion.
        Bottom pixel coordinate

        :return: The bottom of this DocumentRegion.
        :rtype: int
        """
        return self._bottom

    @bottom.setter
    def bottom(self, bottom):
        """
        Sets the bottom of this DocumentRegion.
        Bottom pixel coordinate

        :param bottom: The bottom of this DocumentRegion.
        :type: int
        """

        self._bottom = bottom

    def to_dict(self):
        """
        Returns the model properties as a dict
        """
        result = {}

        for attr, _ in iteritems(self.swagger_types):
            value = getattr(self, attr)
            if isinstance(value, list):
                result[attr] = list(map(
                    lambda x: x.to_dict() if hasattr(x, "to_dict") else x,
                    value
                ))
            elif hasattr(value, "to_dict"):
                result[attr] = value.to_dict()
            elif isinstance(value, dict):
                result[attr] = dict(map(
                    lambda item: (item[0], item[1].to_dict())
                    if hasattr(item[1], "to_dict") else item,
                    value.items()
                ))
            else:
                result[attr] = value

        return result

    def to_str(self):
        """
        Returns the string representation of the model
        """
        return pformat(self.to_dict())

    def __repr__(self):
        """
        For `print` and `pprint`
        """
        return self.to_str()

    def __eq__(self, other):
        """
        Returns true if both objects are equal
        """
        if not isinstance(other, DocumentRegion):
            return False

        return self.__dict__ == other.__dict__

    def __ne__(self, other):
        """
        Returns true if both objects are not equal
        """
        return not self == other
