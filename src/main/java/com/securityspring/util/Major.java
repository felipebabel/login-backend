package com.securityspring.util;


import java.io.Serializable;

public record Major(Long id, String name, String country, String description) implements Serializable {

}
