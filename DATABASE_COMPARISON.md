# Database Comparison: Firebase Realtime Database vs Cloud Firestore

This document explains the architecture, strengths, and rationales for using both **Firebase Realtime Database (RTDB)** and **Cloud Firestore** in the **Load-shedding Tracker** Android application for course **CSE 2200 (Advanced Programming)**.

---

## 1. Architectural Role in Load-shedding Tracker

| Feature | Database Engine | Rationale |
| :--- | :--- | :--- |
| **Live Community Reports** (`/reports`) | **Firebase Realtime Database** | Real-time crowd-sourced reports from citizens (e.g. blown transformers, sudden tripping) require instantaneous, low-overhead synchronization with minimal latency. RTDB's persistent WebSocket connection delivers sub-second updates across thousands of connected clients. |
| **Outage History Records** (`outage_history`) | **Cloud Firestore** | Historical outage logs require structured queries, indexing, and multi-parameter filtering (e.g., `whereEqualTo("area", selectedArea).orderBy("timestamp")`). Firestore provides powerful document-level querying, indexing, and deep offline querying capabilities. |

---

## 2. In-Depth Comparison Matrix

| Attribute | Firebase Realtime Database (RTDB) | Cloud Firestore |
| :--- | :--- | :--- |
| **Data Model** | Single, large JSON tree. Nested objects become sub-branches. | Document-and-Collection model. Collections contain documents, which can contain subcollections. |
| **Data Granularity** | Non-shallow reads: Querying a parent node downloads all of its child nodes. | Shallow reads: Fetching a document does not download subcollections. Reduces data consumption. |
| **Querying Capabilities** | Basic filtering and sorting on a single child key per query. Compound queries are not natively supported. | Advanced compound queries (`whereEqualTo`, `whereGreaterThan`, `orderBy`, etc.) with automatic or composite indexes. |
| **Real-time Updates** | Value and child listeners (`addValueEventListener`, `addChildEventListener`) over persistent WebSockets. | Snapshot listeners (`addSnapshotListener`) providing real-time document and query change notifications. |
| **Offline Persistence** | Basic local caching enabled via `setPersistenceEnabled(true)`. Does not index queries offline. | Full offline synchronization and querying on Android, allowing smooth offline usage. |
| **Scalability** | Scale limited to ~200,000 concurrent connections per database instance (requires sharding). | Automatically scales across multi-region clusters; supports millions of concurrent connections. |
| **Pricing Model** | Charged primarily based on storage (GB) and outgoing bandwidth (GB downloaded). | Charged primarily based on operations: Number of Document Reads, Writes, and Deletes. |
| **App Feature Location** | Screen: `LiveReportsActivity.java` & `ReportOutageActivity.java` | Screen: `OutageHistoryActivity.java` & `LogOutageActivity.java` |

---

## 3. Key Viva Questions & Answers

### Q1: Why not store everything in a single database?
> **Answer:** Each database has distinct cost and query trade-offs. RTDB is ideal for high-frequency, simple status updates (live outage crowdsourcing) where WebSocket streaming is optimal. Firestore is ideal for analytical, historical data requiring multi-field sorting, filtering by district/area, and structured document storage.

### Q2: What is the risk of deeply nested JSON in RTDB?
> **Answer:** Because RTDB reads are not shallow, querying `/` or a high-level node downloads all nested data under it. To avoid performance degradation, data in RTDB should always be normalized (flattened) into separate top-level keys like `/reports`, `/users`, and `/schedules`.

### Q3: How does Firestore handle compound queries?
> **Answer:** Firestore requires a Composite Index when performing queries that combine range comparisons or equality checks with `orderBy()` across multiple fields (e.g., `area == "Khulna"` sorted by `timestamp`). The Firebase console provides a direct link in Logcat to create required composite indexes with one click.

