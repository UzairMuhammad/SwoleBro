import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.swolebro.R
import com.swolebro.view.ui.theme.BlueDark
import com.swolebro.view.ui.theme.GreyLight


@Composable
fun CustomOutlinedTextField(
    value: String,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    onValueChange: (String) -> Unit = {},
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    readOnly: Boolean = false,
) {
    TextField(
        value = value,
        textStyle = MaterialTheme.typography.bodyMedium,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        trailingIcon = trailingIcon,
        modifier = modifier,
        readOnly = readOnly,
        shape = RoundedCornerShape(15),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
        ),
        isError = isError,
    )
}

@Composable
fun CustomOutlinedTextFieldWithError(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    error: String = "",
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        CustomOutlinedTextField(
            value,
            label,
            modifier = modifier.fillMaxWidth(),
            onValueChange,
            visualTransformation,
            keyboardOptions,
            trailingIcon,
            error.isNotEmpty()
        )

        if (error.isNotEmpty()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = error,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun EditableTextFieldWithError(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onClick: (String) -> Unit,
    error: String = "",
) {
    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ){
            CustomOutlinedTextField(
                label = label,
                value = value,
                isError = error.isNotEmpty(),
                onValueChange = onValueChange,
                modifier = Modifier
            )
            Icon(
                Icons.Filled.Check,
                tint = MaterialTheme.colorScheme.onBackground,
                contentDescription = "Submit",
                modifier = Modifier
                    .clickable{onClick(value)}
                    .size(32.dp)
            )
        }

        if (error.isNotEmpty()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = error,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CustomPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    error: String = ""
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val visualTransformation = if (!passwordVisible) PasswordVisualTransformation() else VisualTransformation.None
    val trailingIcon: @Composable (() -> Unit) = {
        val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
        val description = if (passwordVisible) "Hide password" else "Show password"
        IconButton(onClick = { passwordVisible = !passwordVisible }) {
            Icon(imageVector = image, contentDescription = description, tint = MaterialTheme.colorScheme.onBackground)
        }
    }

    CustomOutlinedTextFieldWithError(
        value = value,
        onValueChange = onValueChange,
        label = label,
        visualTransformation = visualTransformation,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = trailingIcon,
        modifier = modifier,
        error = error,
    )
}

@Composable
fun SignInSwoleBroHeader() {
    Text(
        text = "Time to get\nSwoleBro.",
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(0.dp, 48.dp, 0.dp, 0.dp)
    )
}

@Composable
fun CustomButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSecondaryContainer,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(25),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = color,
        ),
        modifier = modifier
            .padding(0.dp, 10.dp, 0.dp, 0.dp)
            .width(175.dp)
            .height(56.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(1.dp))
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelLarge,
            fontSize = MaterialTheme.typography.labelMedium.fontSize
        )
    }
}

@Composable
fun SignInGoogleButton(onClick: () -> Unit){
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(25),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
        modifier = Modifier
            .padding(0.dp, 10.dp, 0.dp, 0.dp)
            .width(175.dp)
            .height(56.dp)
            .shadow(6.dp, RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(1.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_google),
            contentDescription = ""
        )
        Text("Sign in with Google", style = MaterialTheme.typography.labelSmall)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(name: String) {
    val firstName = name.split(" ")[0]
    Box(
        modifier = Modifier.height(80.dp).background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(15.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                Text("Hi ${firstName},", style = MaterialTheme.typography.headlineSmall)
                Text("Let's get", style = MaterialTheme.typography.headlineMedium)
            }
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                Text("SwoleBro", style = MaterialTheme.typography.headlineLarge)
            }
        }
    }
}

@Composable
fun Streak(streakLength: Number, size: Int = 40, color: Color = BlueDark) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$streakLength",
            color = color,
            fontSize = size.sp,
            lineHeight = .8.em
        )
        Icon(
            SweatDroplets,
            contentDescription = "Streak",
            tint = color,
            modifier = Modifier.height(size.dp).width(size.dp)
        )
    }
}

@Composable
fun CustomSurface(content: @Composable() () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        elevation = 8.dp,
        color = MaterialTheme.colorScheme.background
    ) {content()}
}

@Composable
fun TextWithShadow(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box{
        Text(
            text = text,
            style = style,
            color = GreyLight,
            modifier = modifier
                .offset(
                    x = 1.dp,
                    y = 1.dp
                )
                .alpha(0.5f)
        )
        Text(
            text = text,
            style = style,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = color,
            modifier = modifier
        )
    }
}
