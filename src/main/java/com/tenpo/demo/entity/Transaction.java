package com.tenpo.demo.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty
    @Schema(name = "amount", description = "Amount of the transaction", example = "100")
    @NotNull
    @Column(name = "amount")
    private int amount;

    @JsonProperty
    @Schema(name = "trade", description = "Trade of the transaction", example = "Buy")
    @NotBlank
    @Column(name = "trade", length = 50)
    private String trade;

    @JsonProperty
    @Schema(name = "userName", description = "User name", example = "John")
    @NotBlank
    @Column(name = "user_name", length = 50)
    private String userName;

    @JsonProperty
    @Schema(name = "createdAt", description = "Created at", example = "2021-10-10")
    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "created_at")
    private Date createdAt = new Date();
}
