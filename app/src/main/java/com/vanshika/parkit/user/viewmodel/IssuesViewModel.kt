package com.vanshika.parkit.user.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.vanshika.parkit.user.data.model.IssuesDataClass
import com.vanshika.parkit.user.data.repository.IssuesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IssuesViewModel @Inject constructor(
    private val issuesRepository: IssuesRepository
): ViewModel(){
    var allIssues = mutableStateListOf<IssuesDataClass>()
        private set

    var userIssues = mutableStateListOf<IssuesDataClass>()
        private set

    init {
        fetchAllIssues()
    }

    fun addIssue(issue: IssuesDataClass) = issuesRepository.addIssue(issue)

    fun fetchAllIssues(){
        issuesRepository.fetchAllIssues { issues ->
            allIssues.clear()
            allIssues.addAll(issues)
        }
    }

    fun fetchUserIssue(userId: String){
        issuesRepository.fetchUserIssues(userId){issues ->
            userIssues.clear()
            userIssues.addAll(issues)
        }
    }

    fun getIssueById(issueId: String): IssuesDataClass? {
        return allIssues.find { it.issueId == issueId }
    }

    fun updateIssueStatus(issueId: String, newStatus: String) = issuesRepository.updateIssueStatus(issueId, newStatus)

    fun deleteIssue(issueId: String) = issuesRepository.deleteIssue(issueId)
}