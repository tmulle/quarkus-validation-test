package org.acme;

import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Set;

@Path("/validate")
public class GreetingResource {

    @Inject
    Validator validator;

    @POST
    @Path("/form/manual")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response handleFormEncodedManual(HelloRequest request) {
        Set<ConstraintViolation<HelloRequest>> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }

        return Response.ok("Hello " + request.getName()).build();
    }

    @POST
    @Path("/form/auto")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_PLAIN)
    public Response handleFormEncodedAuto(@Valid HelloRequest request) {
        return Response.ok("Hello " + request.getName()).build();
    }

    @POST
    @Path("/multipart/auto")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response handleMultiFormAuto(@Valid HelloRequest request) {
        return Response.ok("Hello " + request.getName()).build();
    }

    @POST
    @Path("/multipart/manual")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_PLAIN)
    public Response handleMultiFormManual(HelloRequest request) {
        Set<ConstraintViolation<HelloRequest>> errors = validator.validate(request);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }
        return Response.ok("Hello " + request.getName()).build();
    }
    
    @GET
    @Path("/boom")
    @Produces(MediaType.TEXT_PLAIN)
    public Response throwException() {
        throw new RuntimeException("Boom!");
    }
}
