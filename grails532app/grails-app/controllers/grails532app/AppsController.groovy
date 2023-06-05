package grails532app

import grails.rest.RestfulController

import static org.springframework.http.HttpStatus.CREATED

class AppsController extends RestfulController {

    static responseFormats = ['json']

    def appsService

    def permissionsService

    AppsController() {
        super(Apps)
    }

    def save(long w_id, Apps app) {
        response.status = CREATED.value()
        respond appsService.save( w_id, app )
    }

    def updateApp(long w_id, long app_id ) {
        respond appsService.updateApp( w_id, app_id, request.JSON )
    }

    def index(long w_id, Integer max, Integer offset, String filter, String nameLike) {

        def filterparams = [:]
        filterparams.w_id = w_id
        filterparams.max = Math.min(max ?: 10, 100)
        filterparams.offset = offset ?: 0
        filterparams.filter = filter
        filterparams.nameLike = nameLike

        def results = appsService.index(w_id, filterparams)

        def rv = [
                appsList  : results.results,
                appsCount : results.count,
                max       : filterparams.max,
                offset    : filterparams.offset
        ]

        respond rv
    }

    def show(long w_id, long app_id) {
        respond appsService.show(w_id, app_id)
    }

    def getImage(long app_id, String sig) {
        render file: appsService.getImage( app_id, sig ), contentType: 'image/png'
    }

    // Permissions

    def grantPermission(long app_id) {
        respond permissionsService.grantPermission( this.safeGetResource(), app_id, request.JSON )
    }

    def revokePermission(long app_id, long p_id) {
        permissionsService.revokePermission(this.safeGetResource(), app_id, p_id)
        render status: 200
    }

    def getPermissions(long app_id) {
        respond permissionsService.getPermissions(this.safeGetResource(), app_id)
    }

    // resource get cleared in autoreload/maven

    def safeGetResource() {
        if ( this.resource ) {
            return this.resource
        }

        GroovyClassLoader gcl = new GroovyClassLoader(getClass().getClassLoader())
        String resourceClassName = this.getClass().getName().replace("Controller", "")
        return gcl.loadClass(resourceClassName, true, false)
    }
}
