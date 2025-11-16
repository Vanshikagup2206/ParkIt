package com.vanshika.parkit.user.viewmodel

import androidx.lifecycle.ViewModel
import com.vanshika.parkit.user.data.model.IssuesDataClass
import com.vanshika.parkit.user.data.repository.IssuesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class IssuesViewModel @Inject constructor(
    private val issuesRepository: IssuesRepository
) : ViewModel() {
    private val _allIssues = MutableStateFlow<List<IssuesDataClass>>(emptyList())
    val allIssues: StateFlow<List<IssuesDataClass>> = _allIssues

    init {
        fetchAllIssues()
    }

    fun addIssue(issue: IssuesDataClass) = issuesRepository.addIssue(issue)

    fun fetchAllIssues() {
        issuesRepository.fetchAllIssues { issues ->
            _allIssues.value = issues
        }
    }

    fun getIssueById(issueId: String): IssuesDataClass? {
        return _allIssues.value.find { it.issueId == issueId }
    }

    fun updateIssueStatus(issueId: String, newStatus: String) =
        issuesRepository.updateIssueStatus(issueId, newStatus)

    fun deleteIssue(issueId: String) = issuesRepository.deleteIssue(issueId)

    fun notifyAdmin(issue: IssuesDataClass) {
        issuesRepository.sendNotificationToAdmin(issue)
    }
}