package inyo.pam.satuoktober.data.remote

import inyo.pam.satuoktober.data.remote.dto.UserDto
import inyo.pam.satuoktober.data.remote.dto.UserResponse

class StudentRemoteDataSource(
    private val api: StudentApiService
) {
    suspend fun getStudents(): UserResponse = api.getUsers()

    suspend fun getStudent(id: Int): UserDto = api.getUser(id)

    suspend fun searchUsers(query: String): UserResponse = api.searchUsers(query)
}
