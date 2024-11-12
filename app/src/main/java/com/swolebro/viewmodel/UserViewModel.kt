package com.swolebro.viewmodel
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.swolebro.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel(){

    //internal
    private val _userCreationResult = MutableLiveData<Result<Boolean>>()
    private val _currentUser = MutableLiveData<FirebaseUser>()

    private val _fullWorkouts = MutableLiveData<MutableList<Workout>>()
    val fullWorkouts: LiveData<MutableList<Workout>> = _fullWorkouts

    val _friends = MutableLiveData<MutableList<Triple<String, String, String>>>()
    val friends: LiveData<MutableList<Triple<String, String, String>>> = _friends

    val _sentRequests = MutableLiveData<MutableList<String>>()
    val sentRequests: LiveData<MutableList<String>> = _sentRequests

    val _receivedRequests = MutableLiveData<MutableList<String>>()
    val receivedRequests: LiveData<MutableList<String>> = _receivedRequests

    private val _templates = MutableLiveData<MutableList<Workout>>()
    val templates: LiveData<MutableList<Workout>> = _templates

    private val _defaultTemplates = MutableLiveData<MutableList<Workout>>()
    val defaultTemplates: LiveData<MutableList<Workout>> = _defaultTemplates

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    val _bmi = MutableStateFlow("0")
    val bmi: StateFlow<String> = _bmi.asStateFlow()

    private val _bmiCat = MutableStateFlow("")
    val bmiCat: StateFlow<String> = _bmiCat.asStateFlow()

    private val _streak = MutableLiveData<String>()
    val streak: LiveData<String> = _streak

    private val _streakGoal = MutableLiveData<String>()
    val streakGoal: LiveData<String> = _streakGoal

    data class UserMeasurements(val height: String, val weight: String)
    private val _userMeasurements = MutableStateFlow(UserMeasurements("0", "0"))
    val userMeasurements: StateFlow<UserMeasurements> = _userMeasurements.asStateFlow()

    private val _isCalculatingBmi = MutableStateFlow(false)
    val isCalculatingBmi: StateFlow<Boolean> = _isCalculatingBmi.asStateFlow()
    private var calculationJob: Job? = null

    private val _defaultUnits = MutableLiveData<String>("kg")
    val defaultUnits: LiveData<String> = _defaultUnits

    private val _idToken = MutableLiveData<String?>()
    val idToken: LiveData<String?> = _idToken

    //public LiveData for observers
    val userCreationResult: LiveData<Result<Boolean>> = _userCreationResult
    val currentUser: LiveData<FirebaseUser> = _currentUser


    fun signUp(email: String, password: String, name: String, onResult: (Result<FirebaseUser>) -> Unit) {
        userRepository.createUser(email, name, password) { repoResult ->
            repoResult.onSuccess { firebaseUser ->
                _currentUser.value = firebaseUser
                _userCreationResult.value = Result.success(repoResult.isSuccess) //redundant?
                Log.d("Sign-up","${firebaseUser.email} successfully signed up")
                onResult(Result.success(firebaseUser))
            }.onFailure { exception ->
                //println("User creation failed: ${exception.message}")
                Log.d("Sign-up", "$email failed to sign up")
                onResult(Result.failure(exception))
            }
        }
    }

    fun logIn(email: String, password: String, onResult: (Result<User>) -> Unit){
        viewModelScope.launch {
            userRepository.signIn(email, password) { result ->
                result.onSuccess { user ->
                    Log.d("Authentication", "${user.email} successfully signed in")
                    onResult(Result.success(user))
                }
                result.onFailure { exception ->
                    Log.d("Authentication", "$email failed to sign in")
                    onResult(Result.failure(exception))
                }
            }
        }
    }

    fun logInNew(email: String, password: String, onResult: (Result<User>) -> Unit){
        viewModelScope.launch {
            val signInResult = userRepository.signInNew(email, password)
            signInResult.onSuccess { user ->
                userRepository.loadUserData()
                Log.d("Authentication", "${user.email} successfully signed in")
                onResult(Result.success(user))
            }.onFailure { exception ->
                Log.d("Authentication", "$email failed to sign in")
                onResult(Result.failure(exception))
            }
        }
    }


    fun logInGoogle(onResult: (Result<User>) -> Unit){
        userRepository.signInGoogle(_idToken.value.toString()) {result ->
            result.onSuccess { user ->
                Log.d("Authentication", "${user.email} successfully signed in with Google")
                getName()
                onResult(Result.success(user))
            }
            result.onFailure { exception ->
                Log.d("Authentication", "$email failed to sign in with Google")
                onResult(Result.failure(exception))
            }
        }
    }

    fun userSnapshot(): User{
        return userRepository.getCurrentUserSnapshot()
    }

    fun setIdToken(token: String?) {
        _idToken.value = token
    }

    fun getUserWorkouts() {
        viewModelScope.launch {
            _fullWorkouts.value = userRepository.getWorkouts()
        }
    }

    fun getFriends() {
        viewModelScope.launch {
            _friends.value = userRepository.getFriends()

        }
    }

    fun getRequestsSent() {
        viewModelScope.launch {
            _sentRequests.value = userRepository.getSentRequests()
        }
    }

    fun getReceivedRequests() {
        viewModelScope.launch {
            _receivedRequests.value = userRepository.getReceivedRequests()
        }
    }

    fun getNameByEmail(email : String){
        userRepository.getNameByEmail(email) { name ->
            Log.d("getnamebyemail", name)
        }
    }


    fun addFriend(email: String) {
        userRepository.addFriend(email)
        getFriends()
        getRequestsSent()
        getReceivedRequests()
    }


    fun removeFriend(email: String) {
        userRepository.removeFriend(email)
    }

    fun getFriendsLatestWorkout(email: String,  callback: (Workout?) -> Unit): Workout? {
        return userRepository.getLatestWorkoutByEmail(email,  callback)
    }

    fun getTemplates() {
        viewModelScope.launch {
            _templates.value = userRepository.getTemplates()
        }
    }

    fun getDefaultTemplates() {
        viewModelScope.launch {
            _defaultTemplates.value = userRepository.getDefaultTemplates()
        }
    }

    fun getName() {
        viewModelScope.launch {
            _name.value = userRepository.getName()
        }
    }

    fun updateName(updatedName: String) {
        _name.value = updatedName
        userRepository.updateName(updatedName)
    }

    fun getEmail() {
        viewModelScope.launch {
            _email.value = userRepository.getEmail()
        }
    }

    fun getHeightWeightBmi(){
        viewModelScope.launch {
            val measurements = userRepository.getHeightWeight()
            _userMeasurements.value = UserMeasurements(measurements[0], measurements[1])
            _bmi.value = measurements[2]
        }
    }

    fun getDefaultUnits(){
        viewModelScope.launch{
            _defaultUnits.value = userRepository.getDefaultUnits()
        }
    }

    fun updateUnits(newUnits: String){
        _defaultUnits.value = newUnits
        userRepository.postUpdatedUnits(newUnits)
    }

    fun updateHeight(changedHeight: String){
        val currentWeight = _userMeasurements.value.weight
        _userMeasurements.value = UserMeasurements(changedHeight, currentWeight)
        userRepository.postNewHeight(changedHeight)
        debounceBmiCalculation()
    }

    fun updateWeight(changedWeight: String){
        val currentHeight = _userMeasurements.value.height
        _userMeasurements.value = UserMeasurements(currentHeight, changedWeight)
        userRepository.postNewWeight(changedWeight)
        debounceBmiCalculation()
    }

    private fun debounceBmiCalculation() {
        calculationJob?.cancel() // Cancel any previous jobs
        calculationJob = viewModelScope.launch {
            _isCalculatingBmi.value = true
            delay(500)
            calculateBMI()
            _isCalculatingBmi.value = false
        }
    }

    fun calculateBMI(){
        _isCalculatingBmi.value = true
        val height = _userMeasurements.value.height
        val weight = _userMeasurements.value.weight
        _bmi.value = userRepository.calculateBMI(height, weight)
    }

    suspend fun getBMICategory(bmi: String){
        _bmiCat.value = BodyMassIndexCategory(bmi)
    }

    fun postNewStreakGoal(streakGoal: Int) {
        userRepository.postNewStreakGoal(streakGoal)
    }

    fun getStreakGoal() {
        viewModelScope.launch {
            _streakGoal.value = userRepository.getStreakGoal()
        }
    }
}