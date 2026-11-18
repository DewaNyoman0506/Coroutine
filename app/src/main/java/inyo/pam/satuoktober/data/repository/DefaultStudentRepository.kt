package inyo.pam.satuoktober.data.repository

import inyo.pam.satuoktober.data.model.Student
import inyo.pam.satuoktober.data.remote.StudentRemoteDataSource
import inyo.pam.satuoktober.data.remote.dto.toStudent

class DefaultStudentRepository(
    private val remoteDataSource: StudentRemoteDataSource
) : StudentRepository {
    override suspend fun getStudents(): Result<List<Student>> {
        return runCatching {
            remoteDataSource.getStudents().users.map { it.toStudent() }
        }
    }

    override suspend fun getStudent(id: Int): Result<Student> {
        return runCatching {
            remoteDataSource.getStudent(id).toStudent()
        }
    }

    override suspend fun searchStudents(query: String): Result<List<Student>> {
        return runCatching {
            remoteDataSource.searchUsers(query).users.map { it.toStudent() }
        }
    }
}
