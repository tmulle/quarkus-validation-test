# quarkus-validation-test

So, I noticed that the @Valid annotation or even a manual throw of ConstraintViolation exception isn't firing my custom ExceptionMapper.

I'm putting together a global exception handler which formats the incoming exceptions to nicely formatted JSON output based on the type of exception being converted.

This works for other exceptions but it seems the `ConstraintViolationException` isn't being handled.

I tried both the JAX-RS way of using the `@Provider` and the `@ServerExceptionMapper` annotation as described in: https://quarkus.io/guides/resteasy-reactive#exception-mapping

I put together a demo at: https://github.com/tmulle/quarkus-validation-test

To test:

*** Normal Exception ***
1.  Call http://localhost:8080/validate/boom - Notice that the exception mapper is called and logs out to the console.

*** FORM encoded and Multipart Form ****
1. POST a form value of `name` with the contents longer that 5 characters to http://localhost:8080/validate/multipart/auto with the `Content-Type` set to `multipart/form-data` - Notice the exception mapper is NOT trigger but you get a 400 response back showing the validation error.

Response:
```
{
	"details": "Error id cc56a09b-9721-4c22-8c4e-e70dcc2c785e-1, jakarta.validation.ConstraintViolationException: name: Name cannot be greater than 5 characters",
	"stack": "jakarta.validation.ConstraintViolationException: name: Name cannot be greater than 5 characters\n\tat org.acme.GreetingResource.handleMultiFormManual(GreetingResource.java:59)\n\tat org.acme.GreetingResource$quarkusrestinvoker$handleMultiFormManual_6f1a1832a12c22edbf8d2eb7eae7d9e20ed15c7f.invoke(Unknown Source)\n\tat org.jboss.resteasy.reactive.server.handlers.InvocationHandler.handle(InvocationHandler.java:29)\n\tat io.quarkus.resteasy.reactive.server.runtime.QuarkusResteasyReactiveRequestContext.invokeHandler(QuarkusResteasyReactiveRequestContext.java:141)\n\tat org.jboss.resteasy.reactive.common.core.AbstractResteasyReactiveContext.run(AbstractResteasyReactiveContext.java:147)\n\tat io.quarkus.vertx.core.runtime.VertxCoreRecorder$14.runWith(VertxCoreRecorder.java:582)\n\tat org.jboss.threads.EnhancedQueueExecutor$Task.run(EnhancedQueueExecutor.java:2513)\n\tat org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1538)\n\tat org.jboss.threads.DelegatingRunnable.run(DelegatingRunnable.java:29)\n\tat org.jboss.threads.ThreadLocalResettingRunnable.run(ThreadLocalResettingRunnable.java:29)\n\tat io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)\n\tat java.base/java.lang.Thread.run(Thread.java:833)"
}
```

2. POST the same data to http://localhost:8080/validate/multipart/auto and this time you get a different response back and still the exception mapper is not triggered.

```
ViolationReport{title='Constraint Violation', status=400, violations=[Violation{field='handleMultiFormAuto.request.name', message='Name cannot be greater than 5 characters'}]}
```

3. POST the same date to http://localhost:8080/validate/form/auto and make sure the Content-Type of `application/x-www-form-urlencoded` and this is the response you get back and still no exception mapper trigger.

```
ViolationReport{title='Constraint Violation', status=400, violations=[Violation{field='handleFormEncodedAuto.request.name', message='Name cannot be greater than 5 characters'}]}
```

4. POST the same data to http://localhost:8080/validate/form/manual and this is the response and no exception mapper trigger.

```
{
	"details": "Error id cc56a09b-9721-4c22-8c4e-e70dcc2c785e-2, jakarta.validation.ConstraintViolationException: name: Name cannot be greater than 5 characters",
	"stack": "jakarta.validation.ConstraintViolationException: name: Name cannot be greater than 5 characters\n\tat org.acme.GreetingResource.handleFormEncodedManual(GreetingResource.java:30)\n\tat org.acme.GreetingResource$quarkusrestinvoker$handleFormEncodedManual_2eb48224fc1a200a215282a28abf27c800482bb1.invoke(Unknown Source)\n\tat org.jboss.resteasy.reactive.server.handlers.InvocationHandler.handle(InvocationHandler.java:29)\n\tat io.quarkus.resteasy.reactive.server.runtime.QuarkusResteasyReactiveRequestContext.invokeHandler(QuarkusResteasyReactiveRequestContext.java:141)\n\tat org.jboss.resteasy.reactive.common.core.AbstractResteasyReactiveContext.run(AbstractResteasyReactiveContext.java:147)\n\tat io.quarkus.vertx.core.runtime.VertxCoreRecorder$14.runWith(VertxCoreRecorder.java:582)\n\tat org.jboss.threads.EnhancedQueueExecutor$Task.run(EnhancedQueueExecutor.java:2513)\n\tat org.jboss.threads.EnhancedQueueExecutor$ThreadBody.run(EnhancedQueueExecutor.java:1538)\n\tat org.jboss.threads.DelegatingRunnable.run(DelegatingRunnable.java:29)\n\tat org.jboss.threads.ThreadLocalResettingRunnable.run(ThreadLocalResettingRunnable.java:29)\n\tat io.netty.util.concurrent.FastThreadLocalRunnable.run(FastThreadLocalRunnable.java:30)\n\tat java.base/java.lang.Thread.run(Thread.java:833)"
}
```



This is with Quarkus 3.4.2 and 3.4.1


This is the resource:
```java
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
```

This is the bean trying to validate:

```java
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
```
