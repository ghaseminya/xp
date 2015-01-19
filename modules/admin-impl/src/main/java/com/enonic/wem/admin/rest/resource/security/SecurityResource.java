package com.enonic.wem.admin.rest.resource.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.exception.NotFoundWebException;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.admin.rest.resource.security.json.CreateGroupJson;
import com.enonic.wem.admin.rest.resource.security.json.CreateRoleJson;
import com.enonic.wem.admin.rest.resource.security.json.CreateUserJson;
import com.enonic.wem.admin.rest.resource.security.json.CreateUserStoreJson;
import com.enonic.wem.admin.rest.resource.security.json.DeletePrincipalJson;
import com.enonic.wem.admin.rest.resource.security.json.DeletePrincipalResultJson;
import com.enonic.wem.admin.rest.resource.security.json.DeletePrincipalsResultJson;
import com.enonic.wem.admin.rest.resource.security.json.EmailAvailabilityJson;
import com.enonic.wem.admin.rest.resource.security.json.GroupJson;
import com.enonic.wem.admin.rest.resource.security.json.PrincipalJson;
import com.enonic.wem.admin.rest.resource.security.json.PrincipalsJson;
import com.enonic.wem.admin.rest.resource.security.json.RoleJson;
import com.enonic.wem.admin.rest.resource.security.json.UpdateGroupJson;
import com.enonic.wem.admin.rest.resource.security.json.UpdateRoleJson;
import com.enonic.wem.admin.rest.resource.security.json.UpdateUserJson;
import com.enonic.wem.admin.rest.resource.security.json.UpdateUserStoreJson;
import com.enonic.wem.admin.rest.resource.security.json.UserJson;
import com.enonic.wem.admin.rest.resource.security.json.UserStoreJson;
import com.enonic.wem.admin.rest.resource.security.json.UserStoresJson;
import com.enonic.wem.api.security.Group;
import com.enonic.wem.api.security.Principal;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.PrincipalKeys;
import com.enonic.wem.api.security.PrincipalQuery;
import com.enonic.wem.api.security.PrincipalQueryResult;
import com.enonic.wem.api.security.PrincipalRelationship;
import com.enonic.wem.api.security.PrincipalRelationships;
import com.enonic.wem.api.security.PrincipalType;
import com.enonic.wem.api.security.Principals;
import com.enonic.wem.api.security.Role;
import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.User;
import com.enonic.wem.api.security.UserStore;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.security.UserStores;
import com.enonic.wem.api.security.acl.UserStoreAccessControlList;

import static com.enonic.wem.api.security.PrincipalQuery.newQuery;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isBlank;


@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "security")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("admin-login")
public final class SecurityResource
    implements AdminResource
{
    private SecurityService securityService;

    @GET
    @Path("userstore/list")
    public UserStoresJson getUserStores()
    {
        final UserStores userStores = securityService.getUserStores();
        return new UserStoresJson( userStores );
    }

    @GET
    @Path("userstore")
    public UserStoreJson getUserStore( @QueryParam("key") final String keyParam )
    {
        if ( keyParam == null )
        {
            return null;
        }

        final UserStoreKey userStoreKey = new UserStoreKey( keyParam );
        final UserStore userStore = securityService.getUserStore( userStoreKey );
        if ( userStore == null )
        {
            throw new NotFoundWebException( String.format( "User Store [%s] not found", keyParam ) );
        }

        final UserStoreAccessControlList userStorePermissions = securityService.getUserStorePermissions( userStoreKey );

        final Principals principals = securityService.getPrincipals( userStorePermissions.getAllPrincipals() );
        return new UserStoreJson( userStore, userStorePermissions, principals );
    }

    @POST
    @Path("userstore/create")
    public UserStoreJson createUserStore( final CreateUserStoreJson params )
    {
        final UserStore userStore = securityService.createUserStore( params.getCreateUserStoreParams() );
        final UserStoreAccessControlList permissions = securityService.getUserStorePermissions( userStore.getKey() );

        final Principals principals = securityService.getPrincipals( permissions.getAllPrincipals() );
        return new UserStoreJson( userStore, permissions, principals );
    }

    @POST
    @Path("userstore/update")
    public UserStoreJson updateUserStore( final UpdateUserStoreJson params )
    {
        final UserStore userStore = securityService.updateUserStore( params.getUpdateUserStoreParams() );
        final UserStoreAccessControlList permissions = securityService.getUserStorePermissions( userStore.getKey() );

        final Principals principals = securityService.getPrincipals( permissions.getAllPrincipals() );
        return new UserStoreJson( userStore, permissions, principals );
    }

    @GET
    @Path("principals")
    public PrincipalsJson findPrincipals( @QueryParam("userStoreKey") final String storeKey, @QueryParam("types") final String types,
                                          @QueryParam("query") final String query )
    {

        UserStoreKey userStoreKey = null;
        if ( StringUtils.isNotEmpty( storeKey ) )
        {
            userStoreKey = new UserStoreKey( storeKey );
        }
        final List<PrincipalType> principalTypes = new ArrayList<>();
        if ( StringUtils.isNotBlank( types ) )
        {
            final String[] typeItems = types.split( "," );
            for ( String typeItem : typeItems )
            {
                try
                {
                    principalTypes.add( PrincipalType.valueOf( typeItem.toUpperCase() ) );
                }
                catch ( IllegalArgumentException e )
                {
                    throw new WebApplicationException( "Invalid principal type: " + typeItem );
                }
            }
        }
        final Principals principals = securityService.findPrincipals( userStoreKey, principalTypes, query );
        return new PrincipalsJson( principals );
    }

    @GET
    @Path("principals/{key: ((role)(:|%3A)([^:(%3A)]+))|((user|group)(:|%3A)([^:(%3A)]+)(:|%3A)([^:(%3A)]+))}")
    public PrincipalJson getPrincipalByKey( @PathParam("key") final String keyParam,
                                            @QueryParam("memberships") final String resolveMembershipsParam )
    {
        final boolean resolveMemberships = "true".equals( resolveMembershipsParam );
        final PrincipalKey principalKey = PrincipalKey.from( keyParam );
        final Optional<? extends Principal> principalResult = securityService.getPrincipal( principalKey );

        if ( !principalResult.isPresent() )
        {
            throw new NotFoundWebException( String.format( "Principal [%s] was not found", keyParam ) );
        }

        final Principal principal = principalResult.get();
        switch ( principalKey.getType() )
        {
            case USER:
                if ( resolveMemberships )
                {
                    final PrincipalKeys membershipKeys = securityService.getMemberships( principalKey );
                    final Principals memberships = securityService.getPrincipals( membershipKeys );
                    return new UserJson( (User) principal, memberships );
                }
                else
                {
                    return new UserJson( (User) principal );
                }

            case GROUP:
                final PrincipalKeys groupMembers = getMembers( principalKey );
                return new GroupJson( (Group) principal, groupMembers );

            case ROLE:
                final PrincipalKeys roleMembers = getMembers( principalKey );
                return new RoleJson( (Role) principal, roleMembers );
        }

        throw new NotFoundWebException( String.format( "Principal [%s] was not found", keyParam ) );
    }

    @GET
    @Path("principals/emailAvailable")
    public EmailAvailabilityJson isEmailAvailable( @QueryParam("userStoreKey") final String userStoreKeyParam,
                                                   @QueryParam("email") final String email )
    {
        if ( isBlank( email ) )
        {
            throw new WebApplicationException( "Expected email parameter" );
        }
        final UserStoreKey userStoreKey = isBlank( userStoreKeyParam ) ? UserStoreKey.system() : new UserStoreKey( userStoreKeyParam );
        final PrincipalQuery query = newQuery().email( email ).userStore( userStoreKey ).build();
        final PrincipalQueryResult queryResult = securityService.query( query );
        return new EmailAvailabilityJson( queryResult.isEmpty() );
    }

    @POST
    @Path("principals/createUser")
    public UserJson createUser( final CreateUserJson params )
    {
        final User user = securityService.createUser( params.getCreateUserParams() );
        final PrincipalKey userKey = user.getKey();
        if ( params.getPassword() != null )
        {
            securityService.setPassword( userKey, params.getPassword() );
        }

        for ( PrincipalKey membershipToAdd : params.getMemberships() )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( membershipToAdd ).to( userKey );
            securityService.addRelationship( rel );
        }

        final Principals memberships = securityService.getPrincipals( securityService.getMemberships( userKey ) );
        return new UserJson( user, memberships );
    }

    @POST
    @Path("principals/createGroup")
    public GroupJson createGroup( final CreateGroupJson params )
    {
        final Group group = securityService.createGroup( params.getCreateGroupParams() );
        final PrincipalKey groupKey = group.getKey();
        for ( PrincipalKey member : params.getMembers() )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( groupKey ).to( member );
            securityService.addRelationship( rel );
        }
        return new GroupJson( group, params.getMembers() );
    }

    @POST
    @Path("principals/createRole")
    public RoleJson createRole( final CreateRoleJson params )
    {
        final Role role = securityService.createRole( params.getCreateRoleParams() );
        final PrincipalKey roleKey = role.getKey();
        for ( PrincipalKey member : params.getMembers() )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( roleKey ).to( member );
            securityService.addRelationship( rel );
        }
        return new RoleJson( role, params.getMembers() );
    }

    @POST
    @Path("principals/updateUser")
    public UserJson updateUser( final UpdateUserJson params )
    {
        final User user = securityService.updateUser( params.getUpdateUserParams() );

        final PrincipalKey userKey = user.getKey();
        for ( PrincipalKey membershipToAdd : params.getAddMemberships() )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( membershipToAdd ).to( userKey );
            securityService.addRelationship( rel );
        }
        for ( PrincipalKey membershipToRemove : params.getRemoveMemberships() )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( membershipToRemove ).to( userKey );
            securityService.removeRelationship( rel );
        }

        final Principals memberships = securityService.getPrincipals( securityService.getMemberships( userKey ) );
        return new UserJson( user, memberships );
    }

    @POST
    @Path("principals/updateGroup")
    public GroupJson updateGroup( final UpdateGroupJson params )
    {
        final Group group = securityService.updateGroup( params.getUpdateGroupParams() );
        final PrincipalKey groupKey = group.getKey();

        updateMemberships( groupKey, params.getRemoveMembers(), params.getAddMembers() );

        final PrincipalKeys groupMembers = getMembers( groupKey );
        return new GroupJson( group, groupMembers );
    }

    @POST
    @Path("principals/updateRole")
    public RoleJson updateRole( final UpdateRoleJson params )
    {
        final Role role = securityService.updateRole( params.getUpdateRoleParams() );
        final PrincipalKey roleKey = role.getKey();

        updateMemberships( roleKey, params.getRemoveMembers(), params.getAddMembers() );

        final PrincipalKeys roleMembers = getMembers( roleKey );
        return new RoleJson( role, roleMembers );
    }

    @POST
    @Path("principals/delete")
    public DeletePrincipalsResultJson deletePrincipals( final DeletePrincipalJson principalKeysParam )
    {
        final DeletePrincipalsResultJson resultsJson = new DeletePrincipalsResultJson();
        principalKeysParam.getKeys().stream().map( PrincipalKey::from ).forEach( ( principalKey ) -> {
            try
            {
                securityService.deletePrincipal( principalKey );
                resultsJson.add( DeletePrincipalResultJson.success( principalKey ) );
            }
            catch ( Exception e )
            {
                resultsJson.add( DeletePrincipalResultJson.failure( principalKey, e.getMessage() ) );
            }
        } );
        return resultsJson;
    }

    private void updateMemberships( final PrincipalKey target, PrincipalKeys membersToRemove, PrincipalKeys membersToAdd )
    {
        for ( PrincipalKey memberToAdd : membersToAdd )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( target ).to( memberToAdd );
            securityService.addRelationship( rel );
        }

        for ( PrincipalKey memberToRemove : membersToRemove )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( target ).to( memberToRemove );
            securityService.removeRelationship( rel );
        }
    }

    private PrincipalKeys getMembers( final PrincipalKey principal )
    {
        final PrincipalRelationships relationships = this.securityService.getRelationships( principal );
        final List<PrincipalKey> members = relationships.stream().map( PrincipalRelationship::getTo ).collect( toList() );
        return PrincipalKeys.from( members );
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

}