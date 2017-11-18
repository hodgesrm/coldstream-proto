/*
 * Goldfin Invoice Processing API
 * Goldfin Invoice Analysis
 *
 * OpenAPI spec version: 1.0.0
 * Contact: rhodges@skylineresearch.comm
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.goldfin.front.invoice.api.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.goldfin.front.invoice.api.model.Document;
import io.goldfin.front.invoice.api.model.Invoice;
import io.goldfin.front.invoice.api.model.OcrScan;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * Root object for invoice content.
 */
@ApiModel(description = "Root object for invoice content.")

public class InvoiceEnvelope   {
  @JsonProperty("id")
  private UUID id = null;

  @JsonProperty("description")
  private String description = null;

  @JsonProperty("tags")
  private String tags = null;

  /**
   * The current processing state of the invoice.  The invoice field is available once the invoice is interpreted.
   */
  public enum StateEnum {
    CREATED("CREATED"),
    
    SCANNED("SCANNED"),
    
    INTERPRETED("INTERPRETED");

    private String value;

    StateEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static StateEnum fromValue(String text) {
      for (StateEnum b : StateEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("state")
  private StateEnum state = null;

  @JsonProperty("source")
  private Document source = null;

  @JsonProperty("ocr")
  private OcrScan ocr = null;

  @JsonProperty("content")
  private Invoice content = null;

  @JsonProperty("creationDate")
  private String creationDate = null;

  public InvoiceEnvelope id(UUID id) {
    this.id = id;
    return this;
  }

  /**
   * Unique invoice id
   * @return id
   **/
  @JsonProperty("id")
  @ApiModelProperty(value = "Unique invoice id")
  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public InvoiceEnvelope description(String description) {
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

  public InvoiceEnvelope tags(String tags) {
    this.tags = tags;
    return this;
  }

  /**
   * A user-provided list of name-value pairs that describe the invoice
   * @return tags
   **/
  @JsonProperty("tags")
  @ApiModelProperty(value = "A user-provided list of name-value pairs that describe the invoice")
  public String getTags() {
    return tags;
  }

  public void setTags(String tags) {
    this.tags = tags;
  }

  public InvoiceEnvelope state(StateEnum state) {
    this.state = state;
    return this;
  }

  /**
   * The current processing state of the invoice.  The invoice field is available once the invoice is interpreted.
   * @return state
   **/
  @JsonProperty("state")
  @ApiModelProperty(value = "The current processing state of the invoice.  The invoice field is available once the invoice is interpreted.")
  public StateEnum getState() {
    return state;
  }

  public void setState(StateEnum state) {
    this.state = state;
  }

  public InvoiceEnvelope source(Document source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
   **/
  @JsonProperty("source")
  @ApiModelProperty(value = "")
  public Document getSource() {
    return source;
  }

  public void setSource(Document source) {
    this.source = source;
  }

  public InvoiceEnvelope ocr(OcrScan ocr) {
    this.ocr = ocr;
    return this;
  }

  /**
   * Get ocr
   * @return ocr
   **/
  @JsonProperty("ocr")
  @ApiModelProperty(value = "")
  public OcrScan getOcr() {
    return ocr;
  }

  public void setOcr(OcrScan ocr) {
    this.ocr = ocr;
  }

  public InvoiceEnvelope content(Invoice content) {
    this.content = content;
    return this;
  }

  /**
   * Get content
   * @return content
   **/
  @JsonProperty("content")
  @ApiModelProperty(value = "")
  public Invoice getContent() {
    return content;
  }

  public void setContent(Invoice content) {
    this.content = content;
  }

  public InvoiceEnvelope creationDate(String creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  /**
   * Date invoice record was created
   * @return creationDate
   **/
  @JsonProperty("creationDate")
  @ApiModelProperty(value = "Date invoice record was created")
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
    InvoiceEnvelope invoiceEnvelope = (InvoiceEnvelope) o;
    return Objects.equals(this.id, invoiceEnvelope.id) &&
        Objects.equals(this.description, invoiceEnvelope.description) &&
        Objects.equals(this.tags, invoiceEnvelope.tags) &&
        Objects.equals(this.state, invoiceEnvelope.state) &&
        Objects.equals(this.source, invoiceEnvelope.source) &&
        Objects.equals(this.ocr, invoiceEnvelope.ocr) &&
        Objects.equals(this.content, invoiceEnvelope.content) &&
        Objects.equals(this.creationDate, invoiceEnvelope.creationDate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, description, tags, state, source, ocr, content, creationDate);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class InvoiceEnvelope {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    description: ").append(toIndentedString(description)).append("\n");
    sb.append("    tags: ").append(toIndentedString(tags)).append("\n");
    sb.append("    state: ").append(toIndentedString(state)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    ocr: ").append(toIndentedString(ocr)).append("\n");
    sb.append("    content: ").append(toIndentedString(content)).append("\n");
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
