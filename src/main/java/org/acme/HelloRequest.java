/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import jakarta.validation.constraints.Size;
import org.jboss.resteasy.reactive.RestForm;

/**
 *
 * @author tmulle
 */
public class HelloRequest {
    
    @RestForm
    @Size(max=5, message = "Name cannot be greater than {max} characters")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
