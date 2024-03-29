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
import io.goldfin.admin.service.api.model.InvoiceItem;
import io.goldfin.admin.service.api.model.TagSet;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * Invoice content obtained from document scanning
 */
@ApiModel(description = "Invoice content obtained from document scanning")

public class Invoice   {
  @JsonProperty("id")
  private UUID id = null;

  @JsonProperty("documentId")
  private UUID documentId = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("identifier")
  private String identifier = null;

  @JsonProperty("account")
  private String account = null;

  @JsonProperty("effectiveDate")
  private Date effectiveDate = null;

  @JsonProperty("vendorIdentifier")
  private String vendorIdentifier = null;

  @JsonProperty("subtotalAmount")
  private BigDecimal subtotalAmount = null;

  @JsonProperty("credit")
  private BigDecimal credit = null;

  @JsonProperty("tax")
  private BigDecimal tax = null;

  @JsonProperty("totalAmount")
  private BigDecimal totalAmount = null;

  @JsonProperty("currency")
  private String currency = null;

  @JsonProperty("items")
  private List<InvoiceItem> items = null;

  @JsonProperty("tags")
  private TagSet tags = null;

  @JsonProperty("creationDate")
  private String creationDate = null;

  public Invoice id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Invoice ID
   * @return id
   **/
  @JsonProperty("id")
  @ApiModelProperty(value = "Invoice ID")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public Invoice documentId(UUID documentId) {
    this.documentId = documentId;
    return this;
  }

  /**
   * Source document ID
   * @return documentId
   **/
  @JsonProperty("documentId")
  @ApiModelProperty(value = "Source document ID")
  public UUID getDocumentId() {
    return documentId;
  }

  public void setDocumentId(UUID documentId) {
    this.documentId = documentId;
  }

  public Invoice description(String description) {
    this.description = description;
    return this;
  }

  /**
   * A user-provided description of the invoice
   * @return description
   **/
  @JsonProperty("description")
  @ApiModelProperty(value = "A user-provided description of the invoice")
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Invoice identifier(String identifier) {
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

  public Invoice account(String account) {
    this.account = account;
    return this;
  }

  /**
   * Account to which invoice applies
   * @return account
   **/
  @JsonProperty("account")
  @ApiModelProperty(value = "Account to which invoice applies")
  public String getAccount() {
    return account;
  }

  public void setAccount(String account) {
    this.account = account;
  }

  public Invoice effectiveDate(Date effectiveDate) {
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

  public Invoice vendorIdentifier(String vendorIdentifier) {
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

  public Invoice subtotalAmount(BigDecimal subtotalAmount) {
    this.subtotalAmount = subtotalAmount;
    return this;
  }

  /**
   * Invoice subtotal amount
   * @return subtotalAmount
   **/
  @JsonProperty("subtotalAmount")
  @ApiModelProperty(value = "Invoice subtotal amount")
  public BigDecimal getSubtotalAmount() {
    return subtotalAmount;
  }

  public void setSubtotalAmount(BigDecimal subtotalAmount) {
    this.subtotalAmount = subtotalAmount;
  }

  public Invoice credit(BigDecimal credit) {
    this.credit = credit;
    return this;
  }

  /**
   * Invoice total credits
   * @return credit
   **/
  @JsonProperty("credit")
  @ApiModelProperty(value = "Invoice total credits")
  public BigDecimal getCredit() {
    return credit;
  }

  public void setCredit(BigDecimal credit) {
    this.credit = credit;
  }

  public Invoice tax(BigDecimal tax) {
    this.tax = tax;
    return this;
  }

  /**
   * Invoice tax amount
   * @return tax
   **/
  @JsonProperty("tax")
  @ApiModelProperty(value = "Invoice tax amount")
  public BigDecimal getTax() {
    return tax;
  }

  public void setTax(BigDecimal tax) {
    this.tax = tax;
  }

  public Invoice totalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
    return this;
  }

  /**
   * Invoice total (subtotal + tax)
   * @return totalAmount
   **/
  @JsonProperty("totalAmount")
  @ApiModelProperty(value = "Invoice total (subtotal + tax)")
  public BigDecimal getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(BigDecimal totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Invoice currency(String currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Currency type
   * @return currency
   **/
  @JsonProperty("currency")
  @ApiModelProperty(value = "Currency type")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public Invoice items(List<InvoiceItem> items) {
    this.items = items;
    return this;
  }

  public Invoice addItemsItem(InvoiceItem itemsItem) {
    if (this.items == null) {
      this.items = new ArrayList<InvoiceItem>();
    }
    this.items.add(itemsItem);
    return this;
  }

  /**
   * Invoice items
   * @return items
   **/
  @JsonProperty("items")
  @ApiModelProperty(value = "Invoice items")
  public List<InvoiceItem> getItems() {
    return items;
  }

  public void setItems(List<InvoiceItem> items) {
    this.items = items;
  }

  public Invoice tags(TagSet tags) {
    this.tags = tags;
    return this;
  }

  /**
   * Get tags
   * @return tags
   **/
  @JsonProperty("tags")
  @ApiModelProperty(value = "")
  public TagSet getTags() {
    return tags;
  }

  public void setTags(TagSet tags) {
    this.tags = tags;
  }

  public Invoice creationDate(String creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  /**
   * Date user record was created
   * @return creationDate
   **/
  @JsonProperty("creationDate")
  @ApiModelProperty(value = "Date user record was created")
  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Invoice invoice = (Invoice) o;
    return Objects.equals(this.id, invoice.id) &&
        Objects.equals(this.documentId, invoice.documentId) &&
        Objects.equals(this.description, invoice.description) &&
        Objects.equals(this.identifier, invoice.identifier) &&
        Objects.equals(this.account, invoice.account) &&
        Objects.equals(this.effectiveDate, invoice.effectiveDate) &&
        Objects.equals(this.vendorIdentifier, invoice.vendorIdentifier) &&
        Objects.equals(this.subtotalAmount, invoice.subtotalAmount) &&
        Objects.equals(this.credit, invoice.credit) &&
        Objects.equals(this.tax, invoice.tax) &&
        Objects.equals(this.totalAmount, invoice.totalAmount) &&
        Objects.equals(this.currency, invoice.currency) &&
        Objects.equals(this.items, invoice.items) &&
        Objects.equals(this.tags, invoice.tags) &&
        Objects.equals(this.creationDate, invoice.creationDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, documentId, description, identifier, account, effectiveDate, vendorIdentifier, subtotalAmount, credit, tax, totalAmount, currency, items, tags, creationDate);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Invoice {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    documentId: ").append(toIndentedString(documentId)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
    sb.append("    account: ").append(toIndentedString(account)).append("\n");
    sb.append("    effectiveDate: ").append(toIndentedString(effectiveDate)).append("\n");
    sb.append("    vendorIdentifier: ").append(toIndentedString(vendorIdentifier)).append("\n");
    sb.append("    subtotalAmount: ").append(toIndentedString(subtotalAmount)).append("\n");
    sb.append("    credit: ").append(toIndentedString(credit)).append("\n");
    sb.append("    tax: ").append(toIndentedString(tax)).append("\n");
    sb.append("    totalAmount: ").append(toIndentedString(totalAmount)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    items: ").append(toIndentedString(items)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    creationDate: ").append(toIndentedString(creationDate)).append("\n");
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

