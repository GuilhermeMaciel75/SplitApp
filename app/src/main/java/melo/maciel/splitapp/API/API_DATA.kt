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
    val group_number_participants: Int
)

data class  GroupLogin(
    val login: String,
    val pwd: String,
)

data class GroupInfo(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val group_name: String,
    @SerializedName("description")
    val group_description: String,
    @SerializedName("n_participants")
    val group_number_participants: Int
)

data class GroupResponse(
    @SerializedName("groups")
    val groups: List<GroupInfo>
)


