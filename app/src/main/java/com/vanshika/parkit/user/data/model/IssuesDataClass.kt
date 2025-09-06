package com.vanshika.parkit.user.data.model

data class IssuesDataClass(
    val issueId: String = "",
    val slotId: String = "",
    val zoneName: String = "",
    val issueType: String = "",        // predefined issue ("Lights not working", etc.)
    val customDescription: String = "",// if "Other" is selected
    val reportedBy: String = "",
    val status: String = "Pending",    // Pending / Resolved / In Progress
    val reportedAt: Long = System.currentTimeMillis()
)

internal fun IssuesDataClass.toFireStoreMap() : Map<String, Any?> = mapOf(
    "issueId" to issueId,
    "slotId" to slotId,
    "zoneName" to zoneName,
    "issueType" to issueType,
    "customDescription" to customDescription,
    "reportedBy" to reportedBy,
    "status" to status,
    "reportedAt" to reportedAt
)