package com.github.se.studybuddies.ui.calender

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.se.studybuddies.R
import com.github.se.studybuddies.data.CalendarUiState
import com.github.se.studybuddies.data.DateUtil
import com.github.se.studybuddies.data.getDisplayName
import com.github.se.studybuddies.navigation.NavigationActions
import com.github.se.studybuddies.navigation.Route
import com.github.se.studybuddies.ui.shared_elements.GoBackRouteButton
import com.github.se.studybuddies.ui.shared_elements.Sub_title
import com.github.se.studybuddies.ui.shared_elements.TopNavigationBar
import com.github.se.studybuddies.ui.theme.Blue
import com.github.se.studybuddies.viewModels.CalendarGroupViewModel
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupStudyCalendarApp(
    viewModel: CalendarGroupViewModel,
    groupUid: String,
    navigationActions: NavigationActions
) {
  val uiState by viewModel.uiState.collectAsState()

  Scaffold(
      modifier = Modifier.fillMaxSize().testTag("group_calendar_scaffold"),
      topBar = {
        TopNavigationBar(
            title = { Sub_title(title = "Group Study Calendar") },
            navigationIcon = {
              GoBackRouteButton(navigationActions = navigationActions, "${Route.GROUP}/${groupUid}")
            },
            actions = {})
      },
  ) { padding ->
    Surface(modifier = Modifier.fillMaxSize().padding(padding)) {
      GroupStudyCalendarWidget(
          days = DateUtil.daysOfWeek,
          yearMonth = uiState.yearMonth,
          dates = uiState.dates,
          onPreviousMonthButtonClicked = { prevMonth -> viewModel.to_Previous_Month(prevMonth) },
          onNextMonthButtonClicked = { nextMonth -> viewModel.to_Next_Month(nextMonth) },
          onDateClickListener = { date ->
            navigationActions.navigateTo("${Route.GROUPDAILYPLANNER}/${groupUid}/$date")
          })
    }
  }
}

@Composable
fun GroupStudyCalendarWidget(
    days: Array<String>,
    yearMonth: YearMonth,
    dates: List<CalendarUiState.Date>,
    onPreviousMonthButtonClicked: (YearMonth) -> Unit,
    onNextMonthButtonClicked: (YearMonth) -> Unit,
    onDateClickListener: (CalendarUiState.Date) -> Unit,
) {
  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    GroupHeader(
        yearMonth = yearMonth,
        onPreviousMonthButtonClicked = onPreviousMonthButtonClicked,
        onNextMonthButtonClicked = onNextMonthButtonClicked)
    Spacer(modifier = Modifier.height(8.dp))
    DayLabels(days)
    Spacer(modifier = Modifier.height(8.dp))
    GroupCalendarGrid(dates = dates, onDateClickListener = onDateClickListener)
  }
}

@Composable
fun GroupHeader(
    yearMonth: YearMonth,
    onPreviousMonthButtonClicked: (YearMonth) -> Unit,
    onNextMonthButtonClicked: (YearMonth) -> Unit,
) {
  Box(modifier = Modifier.fillMaxWidth().background(Blue).padding(16.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      IconButton(
          onClick = { onPreviousMonthButtonClicked(yearMonth.minusMonths(1)) },
          modifier = Modifier.testTag("PreviousMonthButton")) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(id = R.string.back),
                tint = Color.White // Ensure the icon color is white
                )
          }
      Text(
          text = yearMonth.getDisplayName(),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyLarge,
          color = Color.White,
          modifier = Modifier.weight(1f).align(Alignment.CenterVertically))
      IconButton(
          onClick = { onNextMonthButtonClicked(yearMonth.plusMonths(1)) },
          modifier = Modifier.testTag("NextMonthButton")) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = stringResource(id = R.string.next),
                tint = Color.White // Ensure the icon color is white
                )
          }
    }
  }
}

@Composable
fun DayLabels(days: Array<String>) {
  Row(modifier = Modifier.fillMaxWidth()) {
    days.forEach { day ->
      Box(modifier = Modifier.weight(1f)) {
        Text(
            text = day,
            style = MaterialTheme.typography.bodyMedium,
            color = Blue,
            modifier = Modifier.align(Alignment.Center).padding(8.dp))
      }
    }
  }
}

@Composable
fun GroupCalendarGrid(
    dates: List<CalendarUiState.Date>,
    onDateClickListener: (CalendarUiState.Date) -> Unit,
) {
  LazyVerticalGrid(
      columns = GridCells.Fixed(7),
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(8.dp)) {
        items(dates.size) { index ->
          val date = dates[index]
          GroupDateCard(
              date = date, onClickListener = onDateClickListener, modifier = Modifier.padding(4.dp))
        }
      }
}

@Composable
fun GroupDateCard(
    date: CalendarUiState.Date,
    onClickListener: (CalendarUiState.Date) -> Unit,
    modifier: Modifier = Modifier
) {
  Card(
      modifier =
          modifier
              .background(
                  color = if (date.isSelected) Blue else Color.Transparent,
                  shape = MaterialTheme.shapes.medium)
              .clickable { onClickListener(date) },
      elevation = 2.dp) {
        Box(contentAlignment = Alignment.Center) {
          Text(
              text = date.dayOfMonth.toString(),
              style = MaterialTheme.typography.bodyMedium,
              color = Blue,
              modifier = Modifier.padding(8.dp))
        }
      }
}
