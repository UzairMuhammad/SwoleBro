import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.swolebro.R
import com.swolebro.model.Workout
import com.swolebro.viewmodel.UserViewModel
import com.swolebro.viewmodel.WorkoutViewModel
import com.swolebro.view.MainActivity
import com.swolebro.view.TemplateExercisesCardPreview
import com.swolebro.view.formatTime
import com.swolebro.view.ui.theme.Neons
import kotlinx.coroutines.delay


@Composable
fun Social(viewModel: UserViewModel, workoutViewModel: WorkoutViewModel) {

    val showDialog = remember { mutableStateOf(false) }
    var friendsLatestWorkout by remember { mutableStateOf<Workout?>(null) }

    var isLoading by remember { mutableStateOf(false) }

    var friendEmail by remember { mutableStateOf("") } // State for the friend's email
    var friendName by remember { mutableStateOf("") } // State for the friend's name

    // Observe the list of friends from the view model
    val friends by viewModel.friends.observeAsState(initial = emptyList())


    val streak by workoutViewModel.streak.observeAsState("")
    var updatedStreak by remember { mutableIntStateOf(-1) }
    LaunchedEffect(streak) {
        if (streak.isEmpty()) {
            workoutViewModel.getStreak()
        } else {
            workoutViewModel.getStreak()
            updatedStreak = streak.toInt()
        }
    }

    LaunchedEffect(friends) {
        if (friends.isEmpty()) {
            viewModel.getFriends()
        } else {
            viewModel.getFriends()
        }
    }

    viewModel.getFriends()
    val sentRequests by viewModel.sentRequests.observeAsState(emptyList()) as MutableState<List<String>>
    viewModel.getRequestsSent()
    val receivedRequests by viewModel.receivedRequests.observeAsState(emptyList()) as MutableState<List<String>>
    viewModel.getReceivedRequests()

    Column(
        modifier = Modifier.padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Streak(updatedStreak)
        Row() {
        Text("Your Friends' Streaks", style = MaterialTheme.typography.headlineMedium)
        Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Refresh",
                modifier = Modifier
                        .clickable {
                            viewModel.getFriends()
                            viewModel.getRequestsSent()
                            viewModel.getReceivedRequests()
                        }

            )
        }

        LazyColumn(
            modifier = Modifier.padding(start = 8.dp).heightIn(0.dp, 128.dp)
        ) {
            items(friends) { friend ->
                Row(

                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info",
                            modifier = Modifier
                                    .clickable {
                                        showDialog.value = true
                                            isLoading = true;
                                            friendsLatestWorkout = null;
                                            var callback: (Workout?) -> Unit =
                                                    { workout ->
                                                        if (workout != null) {
                                                            println(workout.templateName)
                                                        }
                                                            friendsLatestWorkout = workout
                                                            isLoading = false;
                                                            println(friend.first)
                                                            println(friend.second)
                                                            println(friend.third)
                                                    }

                                            viewModel.getFriendsLatestWorkout(friend.third, callback)
                                            friendEmail = ""
                                            friendName = friend.first
                                            viewModel.getRequestsSent()
                                            viewModel.getFriends()


                                    }
                                    .padding(8.dp)
                    )
                    Text("${friend.first} -")
                    Streak(friend.second.toInt(), size = 20, color = MaterialTheme.colorScheme.tertiary)

                }

            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        CustomOutlinedTextFieldWithError(
            value = friendEmail,
            onValueChange = { friendEmail = it },
            label = "Friend's Email",
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // AddFriendButton
            CustomButton(
                onClick = {
                    viewModel.addFriend(friendEmail)
                    friendEmail = ""
                    viewModel.getRequestsSent()
                    viewModel.getFriends()

                },
                "Add Friend",
                modifier = Modifier.weight(2f)

            )
            CustomButton(
                onClick = {
                    viewModel.removeFriend(friendEmail)
                    friendEmail = ""
                },
                "Remove Friend",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(3f)
            )
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Row() {
            Text("Requests Sent", style = MaterialTheme.typography.headlineMedium)
            Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier
                            .clickable {
                                viewModel.getFriends()
                                viewModel.getRequestsSent()
                                viewModel.getReceivedRequests()
                            }

            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(start = 8.dp).heightIn(0.dp, 128.dp)
        ) {
            items(sentRequests) { friend ->
                Text(friend)
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Row() {
            Text("Requests Received", style = MaterialTheme.typography.headlineMedium)
            Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier
                            .clickable {
                                viewModel.getFriends()
                                viewModel.getRequestsSent()
                                viewModel.getReceivedRequests()
                            }

            )
        }
        LazyColumn(
            modifier = Modifier.padding(start = 8.dp).heightIn(0.dp, 128.dp)
        ) {
            items(receivedRequests) { friend ->
                Text(text = friend)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ){
                    CustomButton(
                        onClick = {
                            viewModel.addFriend(friend)
                            friendEmail = ""
                        },
                        "Accept",
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    CustomButton(
                        onClick = {
                            viewModel.removeFriend(friend)
                            friendEmail = ""
                        },
                        "Decline",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
        if (showDialog.value) {

                TemplateDialog(showDialog = showDialog, friendsLatestWorkout, isLoading, friendName)


        }

}


@Composable
fun TemplateDialog(showDialog: MutableState<Boolean>, workout: Workout?, isLoading: Boolean, name: String) {
    if (isLoading) {
        Dialog(onDismissRequest = { showDialog.value = false } ) {
            CustomSurface {
                Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                            "Loading Workout Information...",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                    )
                    CircularProgressIndicator()



                }
            }
        }
    }
    else if (workout == null) {
        Dialog(onDismissRequest = { showDialog.value = false } ) {
            CustomSurface {
                Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                            "Workouts",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                            "No workouts have been performed yet!",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Left
                    )


                }
            }
        }
    }
    else {
        Dialog(onDismissRequest = { showDialog.value = false }) {
            CustomSurface {
                Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                            "${name}'s Last Workout",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                            workout.templateName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary
                    )
                    TemplateExercisesCardPreview(workout.exercises, true)
                    if (workout.note.isNotEmpty()) {
                        Text(
                                "Notes: " + workout.note,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Left
                        )
                    }
                    Button(
                            onClick = {
                                showDialog.value = false
                            },
                            modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

