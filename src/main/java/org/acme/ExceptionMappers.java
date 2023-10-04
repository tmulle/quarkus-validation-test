/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

/**
 *
 * @author tmulle
 */
public class ExceptionMappers {
    
    @Inject
    Logger log;

    //@ServerExceptionMapper
    public Response mapException(Exception e) {
        log.infof("Converting exception %s to response%n", e);
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
