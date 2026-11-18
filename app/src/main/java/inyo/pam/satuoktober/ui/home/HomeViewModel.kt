package inyo.pam.satuoktober.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import inyo.pam.satuoktober.data.repository.StudentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: StudentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        observeSearch()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { query ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    try {
                        val result = if (query.isBlank()) {
                            repository.getStudents()
                        } else {
                            repository.searchStudents(query)
                        }
                        val students = result.getOrThrow()
                        _uiState.value = HomeUiState(students = students)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        _uiState.value = HomeUiState(
                            errorMessage = e.message ?: "Pencarian gagal"
                        )
                    }
                }
        }
    }

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            try {
                val result = repository.getStudents()
                val students = result.getOrThrow()
                _uiState.value = HomeUiState(students = students)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    errorMessage = e.message ?: "Gagal mengambil data"
                )
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun searchStudents() {
    }

    fun retry() {
        refresh()
    }

    fun refresh() {
        val query = searchQuery.value
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val result = if (query.isBlank()) {
                    repository.getStudents()
                } else {
                    repository.searchStudents(query)
                }
                val students = result.getOrThrow()
                _uiState.value = HomeUiState(students = students)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    errorMessage = e.message ?: "Request gagal"
                )
            }
        }
    }

    companion object {
        fun factory(repository: StudentRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            }
    }
}
