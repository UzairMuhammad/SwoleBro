package com.swolebro.model

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate

class UserTest {

    private lateinit var testEmail: String
    private lateinit var testUid: String
    private lateinit var testUser: User

    @Before
    fun setUp() {
        testEmail = "testuser@swolebro.com"
        testUid = "testBro1"
        testUser = User(userId=testUid, email=testEmail)
    }

    @Test
    fun getUserId() {
        assertEquals(testUid, testUser.userId)
    }

    @Test
    fun setUserId() {
        testUser.userId = testUid.replace('1', '2')
        assertEquals("testBro2", testUser.userId)
    }

    @Test
    fun getEmail() {
        assertEquals(testEmail, testUser.email)
    }

    @Test
    fun setEmail() {
        testUser.email = testEmail.replace("com", "ca")
        assertEquals("testuser@swolebro.ca", testUser.email)
    }

    @Test
    fun getAndSetName() {
        assertNull(testUser.name)
        testUser.name = "First Last"
        assertEquals("First Last", testUser.name)
    }

    @Test
    fun getStreak() {
        assertEquals(0, testUser.streak)
    }

    @Test
    fun setStreak() {
        testUser.streak += 1
        assertEquals(1, testUser.streak)
    }

    @Test
    fun getStreak_goal() {
        assertEquals(3, testUser.streak_goal) //Default Streak Goal for each user
    }

    @Test
    fun setStreak_goal() {
        testUser.streak_goal = 5 //User wants to modify how many workouts they want to aim to log in a week
        assertEquals(5, testUser.streak_goal)
    }

    @Test
    fun getStreak_start() {
        assertNotNull(testUser.streak_start) //Should always be initialized as the most recently passed Sunday
    }

    @Test
    fun setStreak_start() {
        val newStart = "2024-01-01" //New Year
        testUser.streak_start = newStart
        assertEquals(newStart, testUser.streak_start)
    }

    @Test
    fun getStreak_end() {
        assertNotNull(testUser.streak_end) //Should always be initialized as the upcoming Saturday of the current week
    }

    @Test
    fun setStreak_end() {
        val newEnd = "2024-01-07" //end of week 1
        testUser.streak_end = newEnd
        assertEquals(newEnd, testUser.streak_end)
    }

    @Test
    fun getDefaultUnits() {
        assertNotNull(testUser.defaultUnits)
        assertEquals("kg", testUser.defaultUnits)
    }

    @Test
    fun setDefaultUnits() {
        val newUnits = "lbs"
        testUser.defaultUnits = newUnits
        assertEquals(newUnits, testUser.defaultUnits)
    }

    @Test
    fun getWeight() {
        assertTrue(testUser.weight.isEmpty()) //empty on user initialization
    }

    @Test
    fun setWeight() {
        testUser.weight = "78" //assuming in the default units
        assertEquals("78", testUser.weight)
    }

    @Test
    fun getHeight() {
        assertTrue(testUser.height.isEmpty()) //empty on user initialization
    }

    @Test
    fun setHeight() {
        testUser.height = "1.78" //metres
        assertEquals("1.78", testUser.height)
    }

    @Test
    fun getBmi() {
        assertTrue(testUser.bmi.isEmpty()) //empty on user initialization
    }

    @Test
    fun setBmi() {
        testUser.bmi = "24.62"
        assertEquals("24.62", testUser.bmi)
    }

    @Test
    fun getFriends() {
        assertTrue(testUser.friends.isEmpty()) //empty on user initialization
    }

    @Test
    fun setFriends(){
        testUser.friends.add(User("testBro2", "anotherUser@swolebro.com"))
        assertFalse(testUser.friends.isEmpty())
    }

    @Test
    fun getSentRequests() {
        assertTrue(testUser.sentRequests.isEmpty()) //empty on user initialization
    }

    @Test
    fun setGetSentRequests(){
        testUser.sentRequests.add(User("testBro2", "anotherUser@swolebro.com"))
        assertFalse(testUser.sentRequests.isEmpty())
    }

    @Test
    fun getReceivedRequests() {
        assertTrue(testUser.receivedRequests.isEmpty()) //empty on user initialization
    }

    @Test
    fun setGetReceivedRequests(){
        testUser.receivedRequests.add(User("testBro2", "anotherUser@swolebro.com"))
        assertFalse(testUser.receivedRequests.isEmpty())
    }

    @Test
    fun getActiveWorkout() {
        assertNull(testUser.activeWorkout) //null on initialization
    }

    @Test
    fun setActiveWorkout() {
        val newWorkout = Workout()
        testUser.activeWorkout = newWorkout
        assertNotNull(testUser.activeWorkout)
        assertEquals(newWorkout, testUser.activeWorkout)
    }

    @Test
    fun getDefault_templates() {
        assertTrue(testUser.default_templates.isEmpty())
    }

    @Test
    fun setDefault_templates() {
        val newDefaultTemplate = Workout()
        newDefaultTemplate.templateName = "Default Template #1"
        testUser.default_templates.add(newDefaultTemplate)
        assertFalse(testUser.default_templates.isEmpty())
        assertEquals("Default Template #1", testUser.default_templates[0].templateName)
    }

    @Test
    fun getTemplates() {
        assertTrue(testUser.templates.isEmpty())
    }

    @Test
    fun setTemplates() {
        val newTemplate = Workout()
        newTemplate.templateName = "Template #1"
        testUser.templates.add(newTemplate)
        assertFalse(testUser.templates.isEmpty())
        assertEquals("Template #1", testUser.templates[0].templateName)
    }

    @Test
    fun getWorkouts() {
        assertTrue(testUser.workouts.isEmpty())
    }

    @Test
    fun setWorkouts() {
        val newWorkoutRecorded = Workout()
        newWorkoutRecorded.date = LocalDate.now().toString()
        newWorkoutRecorded.exercises.add(Exercise("Bench Press", ExerciseType.WEIGHTS))
        testUser.workouts.add(newWorkoutRecorded)
        assertFalse(testUser.workouts.isEmpty())
        assertEquals("Bench Press", testUser.workouts[0].exercises[0].name)
    }

    @Test
    fun componentTest() {
        testUser.name = "First Last"
        testUser.streak = 10
        testUser.streak_goal = 4
        testUser.streak_start = "2024-01-01"
        testUser.streak_end = "2024-01-07"
        testUser.defaultUnits = "lbs"
        testUser.weight = "80"
        testUser.height = "1.82"
        testUser.bmi = "24.15"
        testUser.friends.add(User("testBro2", "anotheruser@swolebro.com"))
        testUser.sentRequests.add(User("testBro3", "anotheruser2@swolebro.com"))
        testUser.receivedRequests.add(User("testBro4", "anotheruser3@swolebro.com"))
        val aWorkout = Workout()
        testUser.activeWorkout = aWorkout
        testUser.default_templates.add(aWorkout)
        testUser.templates.add(aWorkout)
        testUser.workouts.add(aWorkout)

        val (userId, email, name, streak, strealGoal, streakStart, streakEnd, units, weight, height, bmi, friends, sent, recieved, active, default, templates, workouts) = testUser

        assertEquals(testUid, userId)
        assertEquals(testEmail, email)
        assertEquals("First Last", name)
        assertEquals(10, streak)
        assertEquals(4, strealGoal)
        assertEquals("2024-01-01", streakStart)
        assertEquals("2024-01-07", streakEnd)
        assertEquals("lbs", units)
        assertEquals("80", weight)
        assertEquals("1.82", height)
        assertEquals("24.15", bmi)
        assertEquals((User("testBro2", "anotheruser@swolebro.com")), friends[0])
        assertEquals((User("testBro3", "anotheruser2@swolebro.com")), sent[0])
        assertEquals((User("testBro4", "anotheruser3@swolebro.com")), recieved[0])
        assertEquals(aWorkout, active)
        assertEquals(aWorkout, default[0])
        assertEquals(aWorkout, templates[0])
        assertEquals(aWorkout, workouts[0])
    }
    @Test
    fun copy() {
        val copiedUser = testUser.copy()
        assertEquals(testUser, copiedUser) //values same
        assertNotSame(testUser, copiedUser) //not same object in memory
    }

    @Test
    fun testToString() {
        assertNotNull(testUser.toString())
    }

    @Test
    fun testHashCode() {
        val copiedUser = testUser.copy()
        assertEquals(testUser.hashCode(), copiedUser.hashCode())
    }

    @Test
    fun testEquals() {
        val copiedUser = testUser.copy()
        assertTrue(testUser == copiedUser)
    }
}