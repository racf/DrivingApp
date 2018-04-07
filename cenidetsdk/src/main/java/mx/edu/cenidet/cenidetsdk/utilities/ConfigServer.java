package mx.edu.cenidet.cenidetsdk.utilities;

/**
 * Created by Cipriano on 2/14/2018.
 */

public enum  ConfigServer {
    //#Archivo de configuración de parametros...
    //##########################################################
    //#PARAMETERS
    //#HTTP Connection Config
    http_connectiontimeout("50000"),
    http_sotimeout("3000"),
    http_readtimeout("50000"),

    //#Host
    http_host_node("https://smartsdk-web-service.herokuapp.com/api/"),
    //http_host_login("http://207.249.127.96:5000/v3/auth/"),
    http_host_login("https://drivingapp-python.herokuapp.com/"),
    http_host_mongo("https://driving-monitor-service.herokuapp.com/api/"),

    //#Entities
    http_user("user"),
    http_organization("organization"),
    http_zone("zone"),
    http_campus("campus"),

    //#methods
    //-----login----
    http_tokens("tokens"),
    http_login("login"),

    //---generales----
    http_getAllActive("/getAllActive"),
    http_getAllInactive("/getAllInactive"),
    http_delete("/delete");

    //-----organization----
    /*http_organization_getAllActive("/getAllActive"),
    http_organization_getAllInactive("/getAllInactive"),
    http_organization_delete("/delete"),*/
    //-----zone----
    /*http_zone_getAllActive("/getAllActive"),
    http_zone_getAllInactive("/getAllInactive"),
    http_zone_delete("/delete");*/


    private String propiedad;

    private ConfigServer(String propiedad) {
        this.setPropiedad(propiedad);
    }

    public String getPropiedad() {
        return propiedad;
    }

    public void setPropiedad(String propiedad) {
        this.propiedad = propiedad;
    }

  /* public static void main(String[] args) {
       System.out.println(Util.http_attrs.getPropiedad());
    }*/
}
