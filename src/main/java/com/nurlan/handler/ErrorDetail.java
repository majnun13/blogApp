package com.nurlan.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetail<E> {

    private String path;

    private Date createdDate;

    private String hostName;

    private E message;


}
