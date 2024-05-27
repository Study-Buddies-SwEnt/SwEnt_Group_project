package com.github.se.studybuddies.ui.topics

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.Chat
import com.github.se.studybuddies.data.ChatType
import com.github.se.studybuddies.data.ItemArea
import com.github.se.studybuddies.data.TopicFile
import com.github.se.studybuddies.data.TopicFolder
import com.github.se.studybuddies.data.TopicItem
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.ui.theme.White
import com.github.se.studybuddies.viewModels.ChatViewModel
import com.github.se.studybuddies.viewModels.GroupViewModel
import com.github.se.studybuddies.viewModels.TopicViewModel

@Composable
fun TopicScreen(
    groupUID: String,
    topicUID: String,
    groupViewModel: GroupViewModel,
    topicViewModel: TopicViewModel,
    chatViewModel: ChatViewModel,
    navigationActions: NavigationActions
) {
  val loading = remember { mutableStateOf(true) }

<<<<<<< Updated upstream
  topicViewModel.fetchTopicData(topicUID) {loading.value = false}
=======
  topicViewModel.fetchTopicData(topicUID) { loading.value = false }
>>>>>>> Stashed changes
  val topicData by topicViewModel.topic.collectAsState()
  val group by groupViewModel.group.observeAsState()

  val nameState = remember { mutableStateOf(topicData.name) }
  var exercisesState by remember { mutableStateOf(topicData.exercises) }
  val theoryState = remember { mutableStateOf(topicData.theory) }

  val areaState = remember { mutableStateOf(ItemArea.EXERCISES) }

  LaunchedEffect(topicData) {
    nameState.value = topicData.name
    exercisesState = topicData.exercises
    theoryState.value = topicData.theory
  }

  val floatingButtonsVisible = remember { mutableStateOf(false) }
  val folderFieldVisible = remember { mutableStateOf(false) }
  val fileFieldVisible = remember { mutableStateOf(false) }
  val enteredName = remember { mutableStateOf("") }
  val parentUID = remember { mutableStateOf("") }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(nameState.value) },
            navigationIcon = { GoBackRouteButton(navigationActions, "${Route.GROUP}/$groupUID") },
            actions = {
              IconButton(
                  onClick = {
                    navigationActions.navigateTo("${Route.TOPIC_SETTINGS}/$groupUID/$topicUID")
                  },
              ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    tint = Blue,
                    contentDescription = stringResource(R.string.group_option))
              }
            })
      },
      floatingActionButton = {
        Column(horizontalAlignment = Alignment.End) {
          if (floatingButtonsVisible.value) {
            Button(
                modifier = Modifier
                    .width(100.dp)
                    .height(45.dp),
                shape = RoundedCornerShape(50),
                onClick = {
                  folderFieldVisible.value = !folderFieldVisible.value
                  fileFieldVisible.value = false
                  floatingButtonsVisible.value = false
                  enteredName.value = ""
                }) {
                  Text(
                      text = stringResource(R.string.folder),
                      color = White,
                      style = TextStyle(fontSize = 16.sp))
                }
            Spacer(modifier = Modifier.size(7.dp))
            Button(
                modifier = Modifier
                    .width(100.dp)
                    .height(45.dp),
                shape = RoundedCornerShape(50),
                onClick = {
                  fileFieldVisible.value = !fileFieldVisible.value
                  folderFieldVisible.value = false
                  floatingButtonsVisible.value = false
                  enteredName.value = ""
                }) {
                  Text(
                      text = stringResource(R.string.file),
                      color = White,
                      style = TextStyle(fontSize = 16.sp))
                }
          }
          Spacer(modifier = Modifier.size(10.dp))
          Row(
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
              verticalAlignment = Alignment.Bottom,
              horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = {
                      floatingButtonsVisible.value = !floatingButtonsVisible.value
                      folderFieldVisible.value = false
                      fileFieldVisible.value = false
                    },
                    modifier =
                    Modifier
                        .width(64.dp)
                        .height(64.dp)
                        .clip(MaterialTheme.shapes.medium)) {
                      Icon(
                          imageVector = Icons.Default.Add,
                          contentDescription = stringResource(R.string.create_a_topic_item),
                          tint = White)
                    }
              }
        }
      },
      floatingActionButtonPosition = FabPosition.End) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top)) {
              Box(
                  modifier =
                  Modifier
                      .fillMaxWidth()
                      .background(Color.White)
                      .clickable {
                          chatViewModel.setChat(
                              topicData.let {
                                  group?.let { grp ->
                                      Chat(
                                          it.uid,
                                          it.name,
                                          grp.picture,
                                          ChatType.TOPIC,
                                          groupViewModel.members.value!!.toList(),
                                          grp.uid
                                      )
                                  }
                              })
                          navigationActions.navigateTo(Route.CHAT)
                      }
                      .drawBehind {
                          val strokeWidth = 1f
                          val y = size.height - strokeWidth / 2
                          drawLine(
                              Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth
                          )
                      }) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)) {
                      Spacer(modifier = Modifier.size(16.dp))
                      Text(
                          text = stringResource(R.string.chat),
                          modifier = Modifier.align(Alignment.CenterVertically),
                          style = TextStyle(fontSize = 20.sp, lineHeight = 28.sp))
                    }
                  }
              HorizontalDivider(color = Blue, thickness = 2.dp)
              Box(
                  modifier =
                  Modifier
                      .fillMaxWidth()
                      .background(Color.White)
                      .clickable { navigationActions.navigateTo(Route.CHAT) }
                      .drawBehind {
                          val strokeWidth = 1f
                          val y = size.height - strokeWidth / 2
                          drawLine(
                              Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth
                          )
                      }) {
                    Column {
                      Row(
                          horizontalArrangement = Arrangement.SpaceBetween,
                          verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Practice",
                                modifier =
                                Modifier
                                    .weight(1f)
                                    .clickable { areaState.value = ItemArea.EXERCISES }
                                    .padding(horizontal = 16.dp, vertical = 16.dp)
                                    .align(Alignment.CenterVertically),
                                style = TextStyle(fontSize = 20.sp),
                                textAlign = TextAlign.Center)
                            Text(
                                text = "Theory",
                                modifier =
                                Modifier
                                    .weight(1f)
                                    .clickable { areaState.value = ItemArea.THEORY }
                                    .padding(horizontal = 16.dp, vertical = 16.dp)
                                    .align(Alignment.CenterVertically),
                                style = TextStyle(fontSize = 20.sp),
                                textAlign = TextAlign.Center)
                          }
                      HorizontalDivider(
                          modifier =
                          Modifier
                              .align(
                                  if (areaState.value == ItemArea.EXERCISES) Alignment.Start
                                  else Alignment.End
                              )
                              .fillMaxWidth(0.5f),
                          color = Blue,
                          thickness = 4.dp)
                    }
                  }
<<<<<<< Updated upstream
            if (loading.value) {
                BackHandler {}
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val infiniteTransition = rememberInfiniteTransition(label = stringResource(R.string.loading))
                    val angle by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 1000, easing = LinearEasing)
                        ), label = stringResource(R.string.loading)
                    )
                    Canvas(modifier = Modifier.size((100f).dp)) {
                        drawArc(
                            color = Blue,
                            startAngle = angle,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 10f, cap = StrokeCap.Round)
                        )
                    }
                }
            } else {
=======
              if (loading.value) {
                BackHandler {}
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                  val infiniteTransition =
                      rememberInfiniteTransition(label = stringResource(R.string.loading))
                  val angle by
                      infiniteTransition.animateFloat(
                          initialValue = 0f,
                          targetValue = 360f,
                          animationSpec =
                              infiniteRepeatable(
                                  animation = tween(durationMillis = 1000, easing = LinearEasing)),
                          label = stringResource(R.string.loading))
                  Canvas(modifier = Modifier.size((100f).dp)) {
                    drawArc(
                        color = Blue,
                        startAngle = angle,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 10f, cap = StrokeCap.Round))
                  }
                }
              } else {
>>>>>>> Stashed changes
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(0.dp, Alignment.Top),
                    horizontalAlignment = Alignment.Start,
                    content = {
<<<<<<< Updated upstream
                        if (areaState.value == ItemArea.EXERCISES) {
                            items(exercisesState) { topicItem ->
                                TopicContentItem(
                                    topicItem,
                                    folderFieldVisible,
                                    fileFieldVisible,
                                    parentUID,
                                    0,
                                    topicViewModel,
                                    navigationActions
                                )
                            }
                        } else if (areaState.value == ItemArea.THEORY) {
                            items(theoryState.value) { topicItem ->
                                TopicContentItem(
                                    topicItem,
                                    folderFieldVisible,
                                    fileFieldVisible,
                                    parentUID,
                                    0,
                                    topicViewModel,
                                    navigationActions
                                )
                            }
                        }
                    })
            }
=======
                      if (areaState.value == ItemArea.EXERCISES) {
                        items(exercisesState) { topicItem ->
                          TopicContentItem(
                              topicItem,
                              folderFieldVisible,
                              fileFieldVisible,
                              parentUID,
                              0,
                              topicViewModel,
                              navigationActions)
                        }
                      } else if (areaState.value == ItemArea.THEORY) {
                        items(theoryState.value) { topicItem ->
                          TopicContentItem(
                              topicItem,
                              folderFieldVisible,
                              fileFieldVisible,
                              parentUID,
                              0,
                              topicViewModel,
                              navigationActions)
                        }
                      }
                    })
              }
>>>>>>> Stashed changes
            }
        if (folderFieldVisible.value) {
          TopicItemField(
              enteredName = enteredName,
              label = stringResource(R.string.enter_a_folder_name),
              onDone = {
                folderFieldVisible.value = false
                if (enteredName.value.isNotBlank()) {
<<<<<<< Updated upstream
                    loading.value = false
                  topicViewModel.createTopicFolder(
                      enteredName.value, areaState.value, parentUID.value) {loading.value = true}
=======
                  loading.value = false
                  topicViewModel.createTopicFolder(
                      enteredName.value, areaState.value, parentUID.value) {
                        loading.value = true
                      }
>>>>>>> Stashed changes
                }
                enteredName.value = ""
                parentUID.value = ""
              },
              dismiss = {
                folderFieldVisible.value = false
                enteredName.value = ""
                parentUID.value = ""
              })
        }
        if (fileFieldVisible.value) {
          TopicItemField(
              enteredName = enteredName,
              label = stringResource(R.string.enter_a_file_name),
              onDone = {
                fileFieldVisible.value = false
                if (enteredName.value.isNotBlank()) {
<<<<<<< Updated upstream
                    loading.value = false
                  topicViewModel.createTopicFile(
                      enteredName.value, areaState.value, parentUID.value) {loading.value = true}
=======
                  loading.value = false
                  topicViewModel.createTopicFile(
                      enteredName.value, areaState.value, parentUID.value) {
                        loading.value = true
                      }
>>>>>>> Stashed changes
                }
                enteredName.value = ""
                parentUID.value = ""
              },
              dismiss = {
                fileFieldVisible.value = false
                enteredName.value = ""
                parentUID.value = ""
              })
        }
      }
}

@Composable
fun TopicContentItem(
    topicItem: TopicItem,
    folderFieldVisible: MutableState<Boolean>,
    fileFieldVisible: MutableState<Boolean>,
    parentUID: MutableState<String>,
    depth: Int,
    topicViewModel: TopicViewModel,
    navigationActions: NavigationActions
) {
  when (topicItem) {
    is TopicFolder -> {
      FolderItem(
          topicItem,
          folderFieldVisible,
          fileFieldVisible,
          parentUID,
          depth,
          topicViewModel,
          navigationActions)
    }
    is TopicFile -> {
      FileItem(topicItem, depth, topicViewModel, navigationActions)
    }
  }
}

@Composable
fun FileItem(
    fileItem: TopicFile,
    depth: Int,
    topicViewModel: TopicViewModel,
    navigationActions: NavigationActions
) {
  val isUserStrong = remember { mutableStateOf(false) }
  topicViewModel.getIsUserStrong(fileItem.uid) { isUserStrong.value = it }
  Box(
      modifier =
      Modifier
          .fillMaxWidth()
          .padding(start = (40 * depth).dp)
          .background(Color.White)
          .drawBehind {
              val strokeWidth = 1f
              val y = size.height - strokeWidth / 2
              drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
          }
          .clickable {
              Log.d("MyPrint", "Navigating to resources ${fileItem.uid}")
              navigationActions.navigateTo("${Route.TOPICRESOURCES}/${fileItem.uid}")
          }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(start = 36.dp, end = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween) {
              Text(
                  text = fileItem.name,
                  modifier = Modifier.align(Alignment.CenterVertically),
                  style = TextStyle(fontSize = 20.sp),
                  lineHeight = 28.sp)
              Switch(
                  modifier = Modifier.size(20.dp),
                  checked = isUserStrong.value,
                  onCheckedChange = {
                    val newValue = !isUserStrong.value
                    isUserStrong.value = newValue
                    topicViewModel.updateStrongUser(fileItem.uid, newValue)
                  },
                  colors =
                      SwitchDefaults.colors(
                          checkedThumbColor = White,
                          checkedTrackColor = Blue,
                          uncheckedTrackColor = Color.LightGray))
            }
      }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FolderItem(
    folderItem: TopicFolder,
    folderFieldVisible: MutableState<Boolean>,
    fileFieldVisible: MutableState<Boolean>,
    parentUID: MutableState<String>,
    depth: Int,
    topicViewModel: TopicViewModel,
    navigationActions: NavigationActions
) {
  val isExpanded = remember { mutableStateOf(false) }

  Column {
    Box(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(start = (40 * depth).dp)
            .background(Color.White)
            .drawBehind {
                val strokeWidth = 1f
                val y = size.height - strokeWidth / 2
                drawLine(Color.LightGray, Offset(0f, y), Offset(size.width, y), strokeWidth)
            }
            .clickable { isExpanded.value = !isExpanded.value }) {
          Row(
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(6.dp),
              verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.size(10.dp))
                Icon(
                    painter = painterResource(R.drawable.arrow_right_24px),
                    contentDescription = stringResource(R.string.arrow_icon),
                    modifier = Modifier
                        .size(28.dp)
                        .rotate(if (isExpanded.value) 90f else 0f))
                Spacer(modifier = Modifier.size(10.dp))
                Text(
                    text = folderItem.name,
                    modifier = Modifier.align(Alignment.CenterVertically),
                    style = TextStyle(fontSize = 20.sp),
                    lineHeight = 28.sp)
                AddInFolderButton(
                    folderItem.uid,
                    folderFieldVisible,
                    fileFieldVisible,
                    isExpanded,
                    parentUID,
                    depth)
              }
        }
    if (isExpanded.value) {
      Log.d("MyPrint", "isExpanded")
      folderItem.items.forEach { child ->
        TopicContentItem(
            child,
            folderFieldVisible,
            fileFieldVisible,
            parentUID,
            depth + 1,
            topicViewModel,
            navigationActions)
      }
    }
  }
}

@Composable
fun AddInFolderButton(
    uid: String,
    folderFieldVisible: MutableState<Boolean>,
    fileFieldVisible: MutableState<Boolean>,
    childrenExpanded: MutableState<Boolean>,
    parentUID: MutableState<String>,
    depth: Int
) {
  val expandedState = remember { mutableStateOf(false) }
  val screenWidth = LocalConfiguration.current.screenWidthDp
  val offset = DpOffset((if (depth > 0) (screenWidth - 180) else (-16)).dp, 0.dp)
  Box(modifier = Modifier
      .fillMaxWidth()
      .padding(end = 16.dp)) {
    IconButton(
        modifier = Modifier.align(Alignment.CenterEnd),
        onClick = {
          expandedState.value = true
          childrenExpanded.value = true
        }) {
          Icon(
              modifier = Modifier.size(32.dp),
              painter = painterResource(R.drawable.add_square),
              contentDescription = stringResource(R.string.square_add_icon))
        }
  }
  DropdownMenu(
      modifier = Modifier
          .background(Blue)
          .padding(0.dp),
      expanded = expandedState.value,
      onDismissRequest = { expandedState.value = false },
      offset = offset) {
        if (depth <= 0) {
          DropdownMenuItem(
              modifier = Modifier
                  .fillMaxSize()
                  .padding(0.dp),
              onClick = {
                expandedState.value = false
                folderFieldVisible.value = !folderFieldVisible.value
                fileFieldVisible.value = false
                if (folderFieldVisible.value) {
                  parentUID.value = uid
                }
              },
              text = {
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally),
                    text = stringResource(R.string.folder),
                    color = White,
                    style = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Center))
              })
        }
        DropdownMenuItem(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp),
            onClick = {
              expandedState.value = false
              fileFieldVisible.value = !fileFieldVisible.value
              folderFieldVisible.value = false
              if (fileFieldVisible.value) {
                parentUID.value = uid
              }
            },
            text = {
              Text(
                  modifier = Modifier
                      .fillMaxSize()
                      .align(Alignment.CenterHorizontally),
                  text = stringResource(R.string.file),
                  color = White,
                  style = TextStyle(fontSize = 16.sp, textAlign = TextAlign.Center))
            })
      }
}

@Composable
fun TopicItemField(
    enteredName: MutableState<String>,
    label: String,
    onDone: () -> Unit,
    dismiss: () -> Unit
) {
  AlertDialog(
      modifier = Modifier.padding(0.dp),
      onDismissRequest = { dismiss() },
      confirmButton = { TextButton(onClick = { onDone() }) { Text(stringResource(R.string.ok)) } },
      dismissButton = {
        TextButton(onClick = { dismiss() }) { Text(stringResource(R.string.cancel)) }
      },
      text = {
        TextField(
            value = enteredName.value,
            onValueChange = { enteredName.value = it },
            label = { Text(label) },
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = White,
                    unfocusedContainerColor = White,
                    unfocusedLabelColor = Blue,
                    unfocusedIndicatorColor = Blue),
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(0.dp)
                .border(width = 1.dp, color = Blue, shape = RoundedCornerShape(4.dp)),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            singleLine = true)
      })
}
