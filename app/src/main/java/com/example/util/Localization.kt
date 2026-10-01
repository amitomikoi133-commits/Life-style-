package com.example.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    val DATE_FORMAT_DB = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val DATE_FORMAT_DISPLAY = SimpleDateFormat("EEE, dd MMM yyyy", Locale.US)
    val TIME_FORMAT_DISPLAY = SimpleDateFormat("hh:mm a", Locale.US)
    val MONTH_FORMAT = SimpleDateFormat("MMMM yyyy", Locale.US)

    fun getTodayString(): String = DATE_FORMAT_DB.format(Date())

    fun formatDisplayDate(dateStr: String): String {
        return try {
            val date = DATE_FORMAT_DB.parse(dateStr)
            if (date != null) DATE_FORMAT_DISPLAY.format(date) else dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatMillisDate(millis: Long): String {
        return DATE_FORMAT_DISPLAY.format(Date(millis))
    }

    fun formatMillisTime(millis: Long): String {
        return TIME_FORMAT_DISPLAY.format(Date(millis))
    }

    fun getDaysInCurrentMonth(): Int {
        val cal = Calendar.getInstance()
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getRemainingDaysInCurrentMonth(): Int {
        val cal = Calendar.getInstance()
        val currentDay = cal.get(Calendar.DAY_OF_MONTH)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        return (maxDays - currentDay + 1).coerceAtLeast(1)
    }

    fun getCurrentMonthNumber(): Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.MONTH) + 1
    }
}

object AppStrings {
    fun t(key: String, lang: String = "both"): String {
        return when (key) {
            "app_title" -> when (lang) {
                "bn" -> "মাই ট্রেডার লাইফ"
                "en" -> "My Trader Life"
                else -> "My Trader Life | ট্রেডার ডায়েরি"
            }
            "dashboard" -> when (lang) {
                "bn" -> "ড্যাশবোর্ড"
                "en" -> "Dashboard"
                else -> "Dashboard / ড্যাশবোর্ড"
            }
            "journal" -> when (lang) {
                "bn" -> "ট্রেডিং জার্নাল"
                "en" -> "Trading Journal"
                else -> "Trading Journal / জার্নাল"
            }
            "risk_manager" -> when (lang) {
                "bn" -> "রিস্ক ম্যানেজার"
                "en" -> "Risk Manager"
                else -> "Risk Manager / রিস্ক হিসাব"
            }
            "psychology" -> when (lang) {
                "bn" -> "সাইকোলজি ও আবেগ"
                "en" -> "Psychology"
                else -> "Psychology / সাইকোলজি"
            }
            "routine" -> when (lang) {
                "bn" -> "দৈনিক রুটিন ও অভ্যাস"
                "en" -> "Daily Routine"
                else -> "Daily Routine / রুটিন"
            }
            "salah" -> when (lang) {
                "bn" -> "সালাত ট্র্যাকার"
                "en" -> "Salah Tracker"
                else -> "Salah Tracker / সালাত"
            }
            "goals" -> when (lang) {
                "bn" -> "লক্ষ্য ও রোডম্যাপ"
                "en" -> "Goals Roadmap"
                else -> "Goals / লক্ষ্যসমূহ"
            }
            "analytics" -> when (lang) {
                "bn" -> "অ্যানালিটিক্স ও রিপোর্ট"
                "en" -> "Analytics"
                else -> "Analytics / পরিসংখ্যান"
            }
            "daily_review" -> when (lang) {
                "bn" -> "দৈনিক পর্যালোচনা"
                "en" -> "Daily Review"
                else -> "Daily Review / পর্যালোচনা"
            }
            "settings" -> when (lang) {
                "bn" -> "সেটিংস"
                "en" -> "Settings"
                else -> "Settings / সেটিংস"
            }
            "daily_target" -> when (lang) {
                "bn" -> "দৈনিক টার্গেট"
                "en" -> "Daily Target"
                else -> "Daily Target (দৈনিক টার্গেট)"
            }
            "actual_pnl" -> when (lang) {
                "bn" -> "আজকের প্রফিট/লস (ডেমো)"
                "en" -> "Today's P/L (Demo)"
                else -> "Today's P/L / আজকের লাভ-ক্ষতি"
            }
            "discipline_score" -> when (lang) {
                "bn" -> "শৃঙ্খলা স্কোর"
                "en" -> "Discipline Score"
                else -> "Discipline Score / শৃঙ্খলা"
            }
            "salah_progress" -> when (lang) {
                "bn" -> "সালাত অগ্রগতি"
                "en" -> "Salah Progress"
                else -> "Salah / সালাত"
            }
            "routine_progress" -> when (lang) {
                "bn" -> "রুটিন সম্পন্ন"
                "en" -> "Routine Done"
                else -> "Routine / রুটিন"
            }
            "risk_status" -> when (lang) {
                "bn" -> "রিস্ক স্ট্যাটাস"
                "en" -> "Risk Status"
                else -> "Risk Status / ঝুঁকি নিয়ন্ত্রণ"
            }
            "overtrading_status" -> when (lang) {
                "bn" -> "ওভারট্রেডিং অবস্থা"
                "en" -> "Overtrading Check"
                else -> "Overtrading / অতিরিক্ত ট্রেড"
            }
            else -> key
        }
    }
}
