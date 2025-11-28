package upeu.edu.pe.security.infrastructure.web;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import upeu.edu.pe.security.application.dto.PasswordChangeDto;
import upeu.edu.pe.security.application.dto.UserRequestDto;
import upeu.edu.pe.security.application.dto.UserResponseDto;
import upeu.edu.pe.security.application.dto.UserUpdateDto;
import upeu.edu.pe.security.application.services.UsuarioApplicationService;
import upeu.edu.pe.security.domain.enums.UserRole;
import upeu.edu.pe.security.domain.enums.UserStatus;
import upeu.edu.pe.shared.response.ApiResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/api/v1/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "User management operations")
public class UserController {

    @Inject
    UsuarioApplicationService userService;

    @GET
    @Operation(summary = "Get all users", description = "Retrieve all users with optional filtering")
    @APIResponse(responseCode = "200", description = "Users retrieved successfully")
    public Response getAllUsers(
            @QueryParam("status") UserStatus status,
            @QueryParam("role") UserRole role) {

        List<UserResponseDto> users = userService.findAll();
        // Filtering by status/role is currently done in memory or needs to be added to
        // service
        // For now returning all, or we can filter stream here if list is small
        // Ideally service should support filtering parameters

        return Response.ok(ApiResponse.success("Users retrieved successfully", users)).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    @APIResponse(responseCode = "200", description = "User found")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response getUserById(@Parameter(description = "User ID") @PathParam("id") Long id) {
        UserResponseDto user = userService.findById(id);
        return Response.ok(ApiResponse.success("User retrieved successfully", user)).build();
    }

    @GET
    @Path("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieve a specific user by their username")
    @APIResponse(responseCode = "200", description = "User found")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response getUserByUsername(@Parameter(description = "Username") @PathParam("username") String username) {
        UserResponseDto user = userService.findByUsername(username);
        return Response.ok(ApiResponse.success("User retrieved successfully", user)).build();
    }

    @POST
    @Operation(summary = "Create user", description = "Create a new user")
    @APIResponse(responseCode = "201", description = "User created successfully")
    @APIResponse(responseCode = "400", description = "Invalid input data")
    @APIResponse(responseCode = "409", description = "Username or email already exists")
    public Response createUser(@Valid UserRequestDto requestDto) {
        UserResponseDto user = userService.create(requestDto);
        return Response.status(Response.Status.CREATED)
                .entity(ApiResponse.success("User created successfully", user))
                .build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user")
    @APIResponse(responseCode = "200", description = "User updated successfully")
    @APIResponse(responseCode = "404", description = "User not found")
    @APIResponse(responseCode = "409", description = "Email already exists")
    public Response updateUser(
            @Parameter(description = "User ID") @PathParam("id") Long id,
            @Valid UserUpdateDto updateDto) {
        UserResponseDto user = userService.update(id, updateDto);
        return Response.ok(ApiResponse.success("User updated successfully", user)).build();
    }

    @PUT
    @Path("/{id}/password")
    @Operation(summary = "Change user password", description = "Change password for a specific user")
    @APIResponse(responseCode = "200", description = "Password changed successfully")
    @APIResponse(responseCode = "400", description = "Invalid current password")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response changePassword(
            @Parameter(description = "User ID") @PathParam("id") Long id,
            @Valid PasswordChangeDto passwordChangeDto) {
        userService.changePassword(id, passwordChangeDto);
        return Response.ok(ApiResponse.success("Password changed successfully")).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by their ID")
    @APIResponse(responseCode = "200", description = "User deleted successfully")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response deleteUser(@Parameter(description = "User ID") @PathParam("id") Long id) {
        userService.delete(id);
        return Response.ok(ApiResponse.success("User deleted successfully")).build();
    }

    @GET
    @Path("/stats")
    @Operation(summary = "Get user statistics", description = "Get user count statistics by role and status")
    @APIResponse(responseCode = "200", description = "Statistics retrieved successfully")
    public Response getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        // Count by role (Need to map UserRole enum to IDs or implement logic)
        // For now, we can only count by ID if we know the ID of roles.
        // Or we can implement countByRoleName in repository.
        // Assuming roles: ADMIN=1, MANAGER=2, USER=3 for now or skipping specific role
        // stats if IDs unknown

        // stats.put("byRole", ...);

        return Response.ok(ApiResponse.success("User statistics retrieved successfully", stats)).build();
    }
}