package com.swolebro.model

import android.content.Context
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.swolebro.model.User
import com.swolebro.model.UserRepository
import com.swolebro.model.Workout
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

internal class UserRepositoryTest {

    private lateinit var repository: UserRepository
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var context: Context
    private lateinit var testEmail: String
    private lateinit var testName: String
    private lateinit var testPass: String
    private lateinit var testUid: String
    private lateinit var testUser: User
    private lateinit var mockFirebaseAuth: FirebaseAuth
    private lateinit var mockAuthResult: AuthResult
    @Before
    fun setup(){
        auth = mockk()
        db = mockk()
        context = mockk()

        val mockCollection = mockk<CollectionReference>(relaxed = true)
        val mockDocument = mockk<DocumentReference>(relaxed = true)
        every { db.collection("users") } returns mockCollection
        every { mockCollection.document(any()) } returns mockDocument

        repository = UserRepository(auth, db, context)
        testEmail = "testuser@swolebro.com"
        testPass = "swolestBro1!"
        testName = "testUser"
        testUid = "testBro1"
        testUser = User(testUid, testEmail, testName)

        mockFirebaseAuth = mockk()
        mockAuthResult = mockk()
    }
    @Test
    fun checkUserSnapshot() {
        var result: User?
        val expectedUser = testUser.copy()
        repository.restoreCurrentUser(expectedUser)
        result = repository.getCurrentUserSnapshot()
        assertEquals(expectedUser, result)

        expectedUser.name = "newName"
        repository.restoreCurrentUser(expectedUser)
        result = repository.getCurrentUserSnapshot()
        assertNotEquals(testUser, result)
    }

    @Test(expected = UninitializedPropertyAccessException::class)
    fun checkUserSnapshotException() {
        // currentUser is uninitialized
        repository.getCurrentUserSnapshot()
    }

    @Test
    fun checkRestoreUser(){
        repository.restoreCurrentUser(testUser)

        val modifiedUser = testUser.copy(name = "newName")
        repository.restoreCurrentUser(modifiedUser)
        val storedUser = repository.getCurrentUserSnapshot()

        assertEquals("newName", storedUser.name, "The stored user's name should update to 'newName'")
    }
    @Test
    fun checkHasActiveWorkout(){
        testUser.activeWorkout = Workout()
        repository.restoreCurrentUser(testUser)
        val result = repository.hasActiveWorkout()
        assertTrue("Expected hasActiveWorkout to be true for ${testUser.email}", result)
    }

    @Test
    fun checkSetActiveWorkout() {
        val workout = Workout()
        testUser.activeWorkout = null
        repository.restoreCurrentUser(testUser)
        repository.setActiveWorkout(workout)

        val updatedUser = repository.getCurrentUserSnapshot()
        assertEquals(workout, updatedUser.activeWorkout, "Active workout should be updated")
    }

    @Test
    fun checkGetAuth(){
        val result = repository.getAuth()
        assertNotNull("Expected getAuth to return a non-null FirebaseAuth instance.", result)
        assertSame("Expected getAuth to return injected FirebaseAuth instance", auth, result)
    }

    @Test
    fun checkTranslateUser(){
        val mockFirebaseUser = mockk<FirebaseUser>()
        every { mockFirebaseUser.email } returns testEmail
        every { mockFirebaseUser.uid } returns testUid
        val name = testName

        val resultUser = repository.translateUser(name, mockFirebaseUser)
        assertEquals(testEmail, resultUser.email, "Expected emails to match",)
        assertEquals(testUid, resultUser.userId, "Expected userIds to match")
        assertEquals(testName, resultUser.name, "Expected names to match")
    }

}