/*
 * Goldfin Service API
 * REST API for Goldfin Intelligent Invoice Processing
 *
 * OpenAPI spec version: 1.0.0
 * Contact: info@goldfin.io
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.goldfin.admin.service.api.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * An validation result that checks a specific invoice rule
 */
@ApiModel(description = "An validation result that checks a specific invoice rule")

public class InvoiceValidationResult   {
  @JsonProperty("summary")
  private String summary = null;

  @JsonProperty("passed")
  private Boolean passed = null;

  /**
   * The type of validation, which can be related to the invoice itself or an invoice line item.  In the latter case line item fields are included; otherwise line item information is omitted.
   */
  public enum ValidationTypeEnum {
    INVOICE("INVOICE"),
    
    INVOICE_LINE_ITEM("INVOICE_LINE_ITEM");

    private String value;

    ValidationTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ValidationTypeEnum fromValue(String text) {
      for (ValidationTypeEnum b : ValidationTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("validationType")
  private ValidationTypeEnum validationType = null;

  @JsonProperty("details")
  private String details = null;

  @JsonProperty("invoiceId")
  private UUID invoiceId = null;

  @JsonProperty("identifier")
  private String identifier = null;

  @JsonProperty("effectiveDate")
  private Date effectiveDate = null;

  @JsonProperty("vendorIdentifier")
  private String vendorIdentifier = null;

  @JsonProperty("invoiceItemId")
  private String invoiceItemId = null;

  @JsonProperty("invoiceItemResourceId")
  private String invoiceItemResourceId = null;

  public InvoiceValidationResult summary(String summary) {
    this.summary = summary;
    return this;
  }

  /**
   * A concise summary of the check
   * @return summary
   **/
  @JsonProperty("summary")
  @ApiModelProperty(value = "A concise summary of the check")
  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public InvoiceValidationResult passed(Boolean passed) {
    this.passed = passed;
    return this;
  }

  /**
   * If true the check passed
   * @return passed
   **/
  @JsonProperty("passed")
  @ApiModelProperty(value = "If true the check passed")
  public Boolean isPassed() {
    return passed;
  }

  public void setPassed(Boolean passed) {
    this.passed = passed;
  }

  public InvoiceValidationResult validationType(ValidationTypeEnum validationType) {
    this.validationType = validationType;
    return this;
  }

  /**
   * The type of validation, which can be related to the invoice itself or an invoice line item.  In the latter case line item fields are included; otherwise line item information is omitted.
   * @return validationType
   **/
  @JsonProperty("validationType")
  @ApiModelProperty(value = "The type of validation, which can be related to the invoice itself or an invoice line item.  In the latter case line item fields are included; otherwise line item information is omitted.")
  public ValidationTypeEnum getValidationType() {
    return validationType;
  }

  public void setValidationType(ValidationTypeEnum validationType) {
    this.validationType = validationType;
  }

  public InvoiceValidationResult details(String details) {
    this.details = details;
    return this;
  }

  /**
   * Detailed information about the exception
   * @return details
   **/
  @JsonProperty("details")
  @ApiModelProperty(value = "Detailed information about the exception")
  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public InvoiceValidationResult invoiceId(UUID invoiceId) {
    this.invoiceId = invoiceId;
    return this;
  }

  /**
   * ID of invoice to which discrepancy applies
   * @return invoiceId
   **/
  @JsonProperty("invoiceId")
  @ApiModelProperty(value = "ID of invoice to which discrepancy applies")
  public UUID getInvoiceId() {
    return invoiceId;
  }

  public void setInvoiceId(UUID invoiceId) {
    this.invoiceId = invoiceId;
  }

  public InvoiceValidationResult identifier(String identifier) {
    this.identifier = identifier;
    return this;
  }

  /**
   * Invoice identifier
   * @return identifier
   **/
  @JsonProperty("identifier")
  @ApiModelProperty(value = "Invoice identifier")
  public String getIdentifier() {
    return identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public InvoiceValidationResult effectiveDate(Date effectiveDate) {
    this.effectiveDate = effectiveDate;
    return this;
  }

  /**
   * Invoice effective date
   * @return effectiveDate
   **/
  @JsonProperty("effectiveDate")
  @ApiModelProperty(value = "Invoice effective date")
  public Date getEffectiveDate() {
    return effectiveDate;
  }

  public void setEffectiveDate(Date effectiveDate) {
    this.effectiveDate = effectiveDate;
  }

  public InvoiceValidationResult vendorIdentifier(String vendorIdentifier) {
    this.vendorIdentifier = vendorIdentifier;
    return this;
  }

  /**
   * Vendor identifier
   * @return vendorIdentifier
   **/
  @JsonProperty("vendorIdentifier")
  @ApiModelProperty(value = "Vendor identifier")
  public String getVendorIdentifier() {
    return vendorIdentifier;
  }

  public void setVendorIdentifier(String vendorIdentifier) {
    this.vendorIdentifier = vendorIdentifier;
  }

  public InvoiceValidationResult invoiceItemId(String invoiceItemId) {
    this.invoiceItemId = invoiceItemId;
    return this;
  }

  /**
   * Invoice line item ID
   * @return invoiceItemId
   **/
  @JsonProperty("invoiceItemId")
  @ApiModelProperty(value = "Invoice line item ID")
  public String getInvoiceItemId() {
    return invoiceItemId;
  }

  public void setInvoiceItemId(String invoiceItemId) {
    this.invoiceItemId = invoiceItemId;
  }

  public InvoiceValidationResult invoiceItemResourceId(String invoiceItemResourceId) {
    this.invoiceItemResourceId = invoiceItemResourceId;
    return this;
  }

  /**
   * Invoice line item inventory resource ID
   * @return invoiceItemResourceId
   **/
  @JsonProperty("invoiceItemResourceId")
  @ApiModelProperty(value = "Invoice line item inventory resource ID")
  public String getInvoiceItemResourceId() {
    return invoiceItemResourceId;
  }

  public void setInvoiceItemResourceId(String invoiceItemResourceId) {
    this.invoiceItemResourceId = invoiceItemResourceId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    InvoiceValidationResult invoiceValidationResult = (InvoiceValidationResult) o;
    return Objects.equals(this.summary, invoiceValidationResult.summary) &&
        Objects.equals(this.passed, invoiceValidationResult.passed) &&
        Objects.equals(this.validationType, invoiceValidationResult.validationType) &&
        Objects.equals(this.details, invoiceValidationResult.details) &&
        Objects.equals(this.invoiceId, invoiceValidationResult.invoiceId) &&
        Objects.equals(this.identifier, invoiceValidationResult.identifier) &&
        Objects.equals(this.effectiveDate, invoiceValidationResult.effectiveDate) &&
        Objects.equals(this.vendorIdentifier, invoiceValidationResult.vendorIdentifier) &&
        Objects.equals(this.invoiceItemId, invoiceValidationResult.invoiceItemId) &&
        Objects.equals(this.invoiceItemResourceId, invoiceValidationResult.invoiceItemResourceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(summary, passed, validationType, details, invoiceId, identifier, effectiveDate, vendorIdentifier, invoiceItemId, invoiceItemResourceId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceValidationResult {\n");
    
    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
    sb.append("    passed: ").append(toIndentedString(passed)).append("\n");
    sb.append("    validationType: ").append(toIndentedString(validationType)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("    invoiceId: ").append(toIndentedString(invoiceId)).append("\n");
    sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
    sb.append("    effectiveDate: ").append(toIndentedString(effectiveDate)).append("\n");
    sb.append("    vendorIdentifier: ").append(toIndentedString(vendorIdentifier)).append("\n");
    sb.append("    invoiceItemId: ").append(toIndentedString(invoiceItemId)).append("\n");
    sb.append("    invoiceItemResourceId: ").append(toIndentedString(invoiceItemResourceId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

