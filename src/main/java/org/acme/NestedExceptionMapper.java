/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

/**
 *
 * @author tmulle
 */
@Provider
public class NestedExceptionMapper implements ExceptionMapper<Exception> {

    @Inject
    Logger log;
    
    @Override
    public Response toResponse(Exception exception) {
        log.infof("Converting exception %s to response%n", exception);
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
    
}
