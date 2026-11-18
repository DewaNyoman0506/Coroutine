package inyo.pam.satuoktober.data.repository

import inyo.pam.satuoktober.data.model.Student

interface StudentRepository {
    suspend fun getStudents(): Result<List<Student>>
    suspend fun getStudent(id: Int): Result<Student>
    suspend fun searchStudents(query: String): Result<List<Student>>
}
