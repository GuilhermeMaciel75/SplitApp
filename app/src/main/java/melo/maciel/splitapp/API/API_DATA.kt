package melo.maciel.splitapp.API
import com.google.gson.annotations.SerializedName

data class API_DATA(
    val login: String,
    val pwd: String,
)

data class UserData(
    val nome: String,
    val login: String,
    val pwd: String,
    val email: String
)

data class  GroupData(
    val group_name: String,
    val group_description: String,
    val pwd: String,
    val group_number_participants: Int,
    val login: String
)

data class  GroupLogin(
    val login: String,
    val pwd: String,
    val login_user : String
)

data class GetGroup(
    val login_user : String
)

data class GroupInfo(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val group_name: String,
    @SerializedName("description")
    val group_description: String,
    @SerializedName("n_participants")
    val group_number_participants: Int,
    @SerializedName("participants")
    val group_participants: List<String>
)

data class GroupResponse(
    @SerializedName("groups")
    val groups: List<GroupInfo>
)

data class GroupUserResponse(
    @SerializedName("groups")
    val groups: List<GetGroup>
)

data class Participant(
    val name : String,
    val value : Double = 0.0,
    val percentage: Double
)


