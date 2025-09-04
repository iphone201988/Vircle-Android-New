package com.tech.vircle.utils.event

import com.tech.vircle.R
import com.tech.vircle.data.model.CommonModelClass
import com.tech.vircle.data.model.UploadAvtarClass
import java.util.Calendar

object DummyList {


    // add age list
    fun addAgeList(): ArrayList<CommonModelClass> {
        val ageList = ArrayList<CommonModelClass>()
        ageList.add(CommonModelClass("Other"))
        for (age in 13..100) {
            ageList.add(CommonModelClass(age.toString()))
        }
        return ageList
    }


    // add gender list
    fun addGenderSignupList(): ArrayList<CommonModelClass> {
        return arrayListOf(
            CommonModelClass("Male"),
            CommonModelClass("Female"),
            CommonModelClass("Other")
        )
    }

    fun addGenderList(): ArrayList<CommonModelClass> {
        return arrayListOf(
            CommonModelClass("Male"),
            CommonModelClass("Female"),
            CommonModelClass("Robot"),
            CommonModelClass("Other")
        )
    }


    // add Characteristics List
    fun addCharacteristicsList(): ArrayList<CommonModelClass> {
        return arrayListOf(
            CommonModelClass("Other"),
            CommonModelClass("Adaptable"),
            CommonModelClass("Adventurous"),
            CommonModelClass("Ambitious"),
            CommonModelClass("Analytical"),
            CommonModelClass("Appreciative"),
            CommonModelClass("Assertive"),
            CommonModelClass("Attentive"),
            CommonModelClass("Balanced"),
            CommonModelClass("Brave"),
            CommonModelClass("Calm"),
            CommonModelClass("Caring"),
            CommonModelClass("Cautious"),
            CommonModelClass("Charismatic"),
            CommonModelClass("Cheerful"),
            CommonModelClass("Clever"),
            CommonModelClass("Collaborative"),
            CommonModelClass("Committed"),
            CommonModelClass("Compassionate"),
            CommonModelClass("Confident"),
            CommonModelClass("Conscientious"),
            CommonModelClass("Considerate"),
            CommonModelClass("Consistent"),
            CommonModelClass("Cooperative"),
            CommonModelClass("Courageous"),
            CommonModelClass("Courteous"),
            CommonModelClass("Creative"),
            CommonModelClass("Curious"),
            CommonModelClass("Decisive"),
            CommonModelClass("Dedicated"),
            CommonModelClass("Dependable"),
            CommonModelClass("Detail-oriented"),
            CommonModelClass("Determined"),
            CommonModelClass("Diligent"),
            CommonModelClass("Diplomatic"),
            CommonModelClass("Disciplined"),
            CommonModelClass("Discreet"),
            CommonModelClass("Dynamic"),
            CommonModelClass("Easygoing"),
            CommonModelClass("Efficient"),
            CommonModelClass("Empathetic"),
            CommonModelClass("Energetic"),
            CommonModelClass("Enthusiastic"),
            CommonModelClass("Ethical"),
            CommonModelClass("Even-tempered"),
            CommonModelClass("Expressive"),
            CommonModelClass("Fair-minded"),
            CommonModelClass("Faithful"),
            CommonModelClass("Flexible"),
            CommonModelClass("Focused"),
            CommonModelClass("Forgiving"),
            CommonModelClass("Friendly"),
            CommonModelClass("Frugal"),
            CommonModelClass("Generous"),
            CommonModelClass("Gentle"),
            CommonModelClass("Genuine"),
            CommonModelClass("Good-natured"),
            CommonModelClass("Gracious"),
            CommonModelClass("Hardworking"),
            CommonModelClass("Helpful"),
            CommonModelClass("Honest"),
            CommonModelClass("Honorable"),
            CommonModelClass("Humble"),
            CommonModelClass("Humorous"),
            CommonModelClass("Imaginative"),
            CommonModelClass("Independent"),
            CommonModelClass("Industrious"),
            CommonModelClass("Innovative"),
            CommonModelClass("Insightful"),
            CommonModelClass("Intelligent"),
            CommonModelClass("Intuitive"),
            CommonModelClass("Inventive"),
            CommonModelClass("Kind"),
            CommonModelClass("Knowledgeable"),
            CommonModelClass("Logical"),
            CommonModelClass("Loyal"),
            CommonModelClass("Methodical"),
            CommonModelClass("Meticulous"),
            CommonModelClass("Modest"),
            CommonModelClass("Nonjudgmental"),
            CommonModelClass("Objective"),
            CommonModelClass("Open-minded"),
            CommonModelClass("Optimistic"),
            CommonModelClass("Organized"),
            CommonModelClass("Original"),
            CommonModelClass("Outgoing"),
            CommonModelClass("Patient"),
            CommonModelClass("Perceptive"),
            CommonModelClass("Persistent"),
            CommonModelClass("Persuasive"),
            CommonModelClass("Practical"),
            CommonModelClass("Pragmatic"),
            CommonModelClass("Proactive"),
            CommonModelClass("Protective"),
            CommonModelClass("Punctual"),
            CommonModelClass("Rational"),
            CommonModelClass("Realistic"),
            CommonModelClass("Reflective"),
            CommonModelClass("Reliable"),
            CommonModelClass("Resourceful"),
            CommonModelClass("Respectful"),


            )
    }

    // add relationship list
    fun addRelationshipList(): ArrayList<CommonModelClass> {
        return arrayListOf(
            CommonModelClass("Other"),
            CommonModelClass("No Relationship"),
            CommonModelClass("Friend"),
            CommonModelClass("Boyfriend"),
            CommonModelClass("Girlfriend"),
            CommonModelClass("Stranger"),
            CommonModelClass("New acquaintance"),
            CommonModelClass("Husband"),
            CommonModelClass("Wife"),
            CommonModelClass("Mentor"),
            CommonModelClass("Coach"),
            CommonModelClass("Personal Assistant"),
            CommonModelClass("Dad"),
            CommonModelClass("Mother"),
            CommonModelClass("Teacher"),
            CommonModelClass("Manager"),
        )
    }

    // add addExpertiseList List
    fun addExpertiseList(): ArrayList<CommonModelClass> {
        return arrayListOf(
            CommonModelClass("No Expertise"),
            CommonModelClass("Wellness Coach"),
            CommonModelClass("Fitness Trainer"),
            CommonModelClass("Nutrition Guide"),
            CommonModelClass("Career Mentor"),
            CommonModelClass("Business Advisor"),
            CommonModelClass("Study Buddy"),
            CommonModelClass("Language Partner"),
            CommonModelClass("Math Tutor"),
            CommonModelClass("Cooking Expert"),
            CommonModelClass("Science Tutor"),
            CommonModelClass("Writing Coach"),
            CommonModelClass("Emotional Support"),
            CommonModelClass("Life Listener"),
            CommonModelClass("Organizer"),
            CommonModelClass("Productivity Assistant"),
            CommonModelClass("Financial Guide"),
            CommonModelClass("Tech Support"),
            CommonModelClass("Travel Planner"),
            CommonModelClass("Cooking Expert"),
            CommonModelClass("Fashion Stylist"),
            CommonModelClass("News Curator"),
            CommonModelClass("Legal Info Helper"),
            CommonModelClass("Health Information"),
            CommonModelClass("Creativity Coach"),
            CommonModelClass("History Expert"),
            CommonModelClass("Parenting Advisor"),
            CommonModelClass("Movie Geek"),
            CommonModelClass("Music Enthusiast"),
            CommonModelClass("Art Critic"),
            CommonModelClass("Bookworm"),
            CommonModelClass("Theatre Enthusiast"),
            CommonModelClass("Dance Guide"),
            CommonModelClass("World Cuisine Explorer"),
            CommonModelClass("Museum Guide"),
            CommonModelClass("History Aficionado"),
            CommonModelClass("Photography Mentor"),
            CommonModelClass("Festival Finder"),
            CommonModelClass("Other")
        )
    }


    // add CanTextEvery list
    fun addCanTextEveryList(): ArrayList<CommonModelClass> {
        return arrayListOf(
            CommonModelClass("Day"),
            CommonModelClass("Week"),
            CommonModelClass("Month"),
            CommonModelClass("No Schedule")
        )
    }

    // add On list
    fun addOnList(): ArrayList<CommonModelClass> {
        return arrayListOf(
            CommonModelClass("For Day"),

            )
    }

    // add week list
    fun addWeekList(): ArrayList<CommonModelClass> {
        return arrayListOf(
            CommonModelClass("Random"),
            CommonModelClass("Monday"),
            CommonModelClass("Tuesday"),
            CommonModelClass("Wednesday"),
            CommonModelClass("Thursday"),
            CommonModelClass("Friday"),
            CommonModelClass("Saturday"),
            CommonModelClass("Sunday"),

            )
    }


    // add month list
    fun addMonthList(): ArrayList<CommonModelClass> {
        return arrayListOf(
            CommonModelClass("Random"),
            CommonModelClass("1st"),
            CommonModelClass("2st"),
            CommonModelClass("3st"),
            CommonModelClass("4st"),
            CommonModelClass("5st"),
            CommonModelClass("6st"),
            CommonModelClass("7st"),
            CommonModelClass("8st"),
            CommonModelClass("9st"),
            CommonModelClass("10st"),
            CommonModelClass("11st"),
            CommonModelClass("12st"),
            CommonModelClass("13st"),
            CommonModelClass("14st"),
            CommonModelClass("15st"),
            CommonModelClass("16st"),
            CommonModelClass("17st"),
            CommonModelClass("18st"),
            CommonModelClass("19st"),
            CommonModelClass("20st"),
            CommonModelClass("21st"),
            CommonModelClass("22st"),
            CommonModelClass("23st"),
            CommonModelClass("24st"),
            CommonModelClass("25st"),
            CommonModelClass("26st"),
            CommonModelClass("27st"),
            CommonModelClass("28st"),
            CommonModelClass("Last day of month"),
        )
    }

    fun Int.toOrdinal(): String {
        if (this in 11..13) return "${this}th" // special case
        return when (this % 10) {
            1 -> "${this}st"
            2 -> "${this}nd"
            3 -> "${this}rd"
            else -> "${this}th"
        }
    }

    fun String.toOrdinalKey(): String {
        return when (this) {
            "Last day of month" -> this
            else -> {
                val num = this.removeSuffix("st").toIntOrNull()
                if (num != null) num.toOrdinal() else this
            }
        }
    }



    // add time list
    fun addTimeList(): ArrayList<CommonModelClass> {
        val list = arrayListOf<CommonModelClass>()
        list.add(CommonModelClass("Random"))
        for (hour in 0..23) {
            val formattedHour = String.format("%02d:00", hour)
            list.add(CommonModelClass(formattedHour))
        }
        return list
    }

    // add WantToHear list
    fun addWantToHearList(): ArrayList<CommonModelClass> {
        return arrayListOf(
            CommonModelClass("Random Message"),
            CommonModelClass("Greeting"),
            CommonModelClass("Daily News"),
            CommonModelClass("Weekly News"),
            CommonModelClass("Daily Motivation"),
            CommonModelClass("Mood Boosters"),
            CommonModelClass("Inspirational Quotes"),
            CommonModelClass("Check-in on Recent"),
            CommonModelClass("Wellness Check-ins"),
            CommonModelClass("Dietary Reminders"),
            CommonModelClass("Water Consumption Reminder"),
            CommonModelClass("Meditation Reminder"),
            CommonModelClass("Stretching Reminder"),
            CommonModelClass("Fun Facts"),
            CommonModelClass("Weather Forecast"),
            CommonModelClass("Movie Recommendations"),
            CommonModelClass("Book Suggestions"),
            CommonModelClass("Study Tips"),
            CommonModelClass("Jokes"),
            CommonModelClass("Role Playing"),
            CommonModelClass("Language Practise"),
            CommonModelClass("Cooking Recipes"),
            CommonModelClass("Other"),
        )
    }

    // add avtar list
    fun addAvtarList(): ArrayList<UploadAvtarClass> {
        return arrayListOf(
            UploadAvtarClass(R.drawable.teacher),
            UploadAvtarClass(R.drawable.mental_icon),
            UploadAvtarClass(R.drawable.cooking_expert),
            UploadAvtarClass(R.drawable.fitness_icon),
            UploadAvtarClass(R.drawable.language_partner),
            UploadAvtarClass(R.drawable.melody),
            UploadAvtarClass(R.drawable.teacher),
            UploadAvtarClass(R.drawable.mental_icon),
            UploadAvtarClass(R.drawable.cooking_expert),
            UploadAvtarClass(R.drawable.fitness_icon),
            UploadAvtarClass(R.drawable.language_partner),
            UploadAvtarClass(R.drawable.melody),
            UploadAvtarClass(R.drawable.teacher),
            UploadAvtarClass(R.drawable.mental_icon),
            UploadAvtarClass(R.drawable.cooking_expert),
            UploadAvtarClass(R.drawable.fitness_icon),
            UploadAvtarClass(R.drawable.language_partner),
            UploadAvtarClass(R.drawable.melody),
            UploadAvtarClass(R.drawable.teacher),
            UploadAvtarClass(R.drawable.mental_icon),
            UploadAvtarClass(R.drawable.cooking_expert),
            UploadAvtarClass(R.drawable.fitness_icon),
            UploadAvtarClass(R.drawable.language_partner),
            UploadAvtarClass(R.drawable.melody),
            UploadAvtarClass(R.drawable.teacher),
            UploadAvtarClass(R.drawable.mental_icon),
            UploadAvtarClass(R.drawable.cooking_expert),
            UploadAvtarClass(R.drawable.fitness_icon),
            UploadAvtarClass(R.drawable.language_partner),
            UploadAvtarClass(R.drawable.melody),

            )
    }

}