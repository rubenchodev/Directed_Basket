package udec.sutatausa.directedbasket.BasicClass

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

data class NewUserObject (
    var policy: Boolean,
    var profile: String,
    var name: String,
    var email: String,
    var coach: String,
    var image: String,
    var height: String,
    var weight: String,
    var birthDay: String
);

/**
 * Permite definir el objeto de un usuario
 */
class UserObject {
    var policy: Boolean? = false;
    var profile: String? = "";
    var name: String? = "";
    var email: String? = "";
    var coach: String? = "";
    var image: String? = "";
    var height: String? = "";
    var weight: String? = "";
    var birthDay: String? = "";

    /**
     * Definimos el constructor
     */
    constructor(policy: Boolean?, profile: String?, name: String?, email: String?, coach: String?, image: String?, height: String?, weight: String?, birthDay: String?) {
        this.policy = policy;
        this.profile = profile;
        this.name = name;
        this.email = email;
        this.coach = coach;
        this.image = image;
        this.height = height;
        this.weight = weight;
        this.birthDay = birthDay;
    }

    constructor()   // **Add this**
}