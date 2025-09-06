package com.vanshika.parkit.user.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.vanshika.parkit.user.data.model.IssuesDataClass
import com.vanshika.parkit.user.data.model.toFireStoreMap
import javax.inject.Inject

class IssuesRepository @Inject constructor(){
    private val collection = FirebaseFirestore.getInstance().collection("issues")

//    fun addIssue(issue: IssuesDataClass){
//        collection.document(issue.issueId)
//            .set(issue.toFireStoreMap())
//    }

    fun addIssue(issue: IssuesDataClass) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email ?: "Unknown"

        // Create a new issue object with reportedBy = logged-in user's email
        val issueWithReporter = issue.copy(reportedBy = userEmail)

        collection.document(issueWithReporter.issueId)
            .set(issueWithReporter.toFireStoreMap())
    }

    fun fetchAllIssues(onIssueChanged: (List<IssuesDataClass>) -> Unit){
        collection.addSnapshotListener{snapshot, _ ->
            val issues = snapshot?.documents?.mapNotNull { doc ->
                doc.toObject(IssuesDataClass::class.java)
            } ?: emptyList()
            onIssueChanged(issues)
        }
    }

    fun fetchUserIssues(userId: String, onIssueChanged: (List<IssuesDataClass>) -> Unit){
        collection.whereEqualTo("reportedBy", userId)
            .addSnapshotListener{snapshot, _ ->
                val issues = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(IssuesDataClass::class.java)
                }?: emptyList()
                onIssueChanged(issues)
            }
    }

    fun updateIssueStatus(issueId: String, newStatus: String){
        collection.document(issueId).update("status", newStatus)
    }

    fun deleteIssue(issueId: String){
        collection.document(issueId).delete()
    }
}