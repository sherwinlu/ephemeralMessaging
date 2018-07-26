package com.sherwin.fitnessPal.messaging.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatInput implements Serializable {
    private long id;
    private String username;
    private String text;
    private Integer timeout;
    private Date expirationDate;
}
