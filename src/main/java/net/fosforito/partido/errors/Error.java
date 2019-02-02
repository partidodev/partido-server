package net.fosforito.partido.errors;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This general error structure is used throughout this API.
 **/
public class Error   {
  
  private  Integer code;
  private  String description;
  private  String reasonPhrase;

  /**
   * minimum: 400
   * maximum: 599
   **/
  public Error code(Integer code) {
    this.code = code;
    return this;
  }

  
  @JsonProperty("code")
  public Integer getCode() {
    return code;
  }
  public void setCode(Integer code) {
    this.code = code;
  }

  /**
   **/
  public Error description(String description) {
    this.description = description;
    return this;
  }

  
  @JsonProperty("description")
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   **/
  public Error reasonPhrase(String reasonPhrase) {
    this.reasonPhrase = reasonPhrase;
    return this;
  }

  
  @JsonProperty("reasonPhrase")
  public String getReasonPhrase() {
    return reasonPhrase;
  }
  public void setReasonPhrase(String reasonPhrase) {
    this.reasonPhrase = reasonPhrase;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Error error = (Error) o;
    return Objects.equals(code, error.code) &&
        Objects.equals(description, error.description) &&
        Objects.equals(reasonPhrase, error.reasonPhrase);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, description, reasonPhrase);
  }

  @Override
  public String toString() {
    String sb = "class Error {\n" +
        "    code: " + toIndentedString(code) + "\n" +
        "    description: " + toIndentedString(description) + "\n" +
        "    reasonPhrase: " + toIndentedString(reasonPhrase) + "\n" +
        "}";
    return sb;
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

