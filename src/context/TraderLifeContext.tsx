import React, { createContext, useContext, useState, useEffect } from 'react';
import {
  Screen,
  Trade,
  SalahRecord,
  Habit,
  HabitLog,
  PsychologyLog,
  MonthlyGoal,
  DailyReview,
  UserSettings,
  Language,
} from '../types';

interface Toast {
  id: string;
  message: string;
  type: 'success' | 'error' | 'info';
}

interface TraderLifeContextType {
  currentScreen: Screen;
  setCurrentScreen: (screen: Screen) => void;
  selectedDate: string;
  setSelectedDate: (date: string) => void;
  language: Language;
  setLanguage: (lang: Language) => void;

  // Settings
  settings: UserSettings;
  updateSettings: (newSettings: Partial<UserSettings>) => void;

  // Trades
  trades: Trade[];
  addTrade: (trade: Omit<Trade, 'id' | 'createdAt'>) => void;
  updateTrade: (trade: Trade) => void;
  deleteTrade: (id: string) => void;

  // Salah
  salahRecords: Record<string, SalahRecord>;
  updateSalahStatus: (date: string, prayer: keyof Omit<SalahRecord, 'date' | 'notes'>, status: SalahRecord['fajr']) => void;

  // Habits
  habits: Habit[];
  habitLogs: Record<string, string[]>; // date -> completed habit IDs
  toggleHabit: (habitId: string, date?: string) => void;
  addCustomHabit: (titleEn: string, titleBn: string, category: string, icon: string) => void;
  deleteHabit: (id: string) => void;

  // Psychology
  psychologyLogs: PsychologyLog[];
  addPsychologyLog: (log: Omit<PsychologyLog, 'id' | 'createdAt'>) => void;

  // Monthly Goals
  goals: MonthlyGoal[];
  updateGoal: (month: number, target: number, notes?: string) => void;

  // Daily Review
  dailyReviews: Record<string, DailyReview>;
  saveDailyReview: (review: DailyReview) => void;

  // UI / Security
  isLocked: boolean;
  unlockApp: (pin: string) => boolean;
  lockApp: () => void;
  toasts: Toast[];
  showToast: (message: string, type?: 'success' | 'error' | 'info') => void;
  removeToast: (id: string) => void;

  // Data Export & Import
  exportData: () => string;
  importData: (jsonData: string) => boolean;
  resetAllData: () => void;
}

const DEFAULT_SETTINGS: UserSettings = {
  traderName: 'Disciplined Trader',
  currency: '$',
  demoBalance: 10000,
  riskPerTradePct: 1.0,
  maxDailyLoss: 200,
  maxTradesPerDay: 3,
  currentMonthTarget: 2.0,
  language: 'both',
  pinLockEnabled: false,
  pinCode: '1234',
  theme: 'dark-gold',
};

const DEFAULT_HABITS: Habit[] = [
  { id: '1', titleEn: 'Wake up on time (Discipline)', titleBn: 'নির্দিষ্ট সময়ে ঘুম থেকে ওঠা', category: 'Routine', icon: 'AlarmClock', order: 1 },
  { id: '2', titleEn: 'Morning Study & Market News', titleBn: 'সকালের পড়াশোনা ও মার্কেট বিশ্লেষণ', category: 'Trading', icon: 'BookOpen', order: 2 },
  { id: '3', titleEn: 'Pre-Market Chart Analysis', titleBn: 'প্রি-মার্কেট চার্ট অ্যানালাইসিস', category: 'Trading', icon: 'CandlestickChart', order: 3 },
  { id: '4', titleEn: 'Focused Trading Session (Rules strictly applied)', titleBn: 'মনোযোগী ট্রেডিং সেশন (রুলস সহ)', category: 'Trading', icon: 'TrendingUp', order: 4 },
  { id: '5', titleEn: 'Physical Exercise / Workout', titleBn: 'ব্যায়াম / শরীরচর্চা', category: 'Health', icon: 'Activity', order: 5 },
  { id: '6', titleEn: 'Book / Mindset Reading (15 mins)', titleBn: 'বই বা মাইন্ডসেট পড়া (১৫ মিনিট)', category: 'Mindset', icon: 'Bookmark', order: 6 },
  { id: '7', titleEn: 'Family & Personal Quality Time', titleBn: 'পরিবার ও নিজের সময়', category: 'Life', icon: 'Heart', order: 7 },
  { id: '8', titleEn: 'Sleep on time (Rest & Recovery)', titleBn: 'সময়মতো ঘুম (সুস্বাস্থ্য)', category: 'Routine', icon: 'Moon', order: 8 },
];

const DEFAULT_GOALS: MonthlyGoal[] = Array.from({ length: 12 }, (_, i) => ({
  month: i + 1,
  targetPerDay: i + 1,
  title: `Month ${i + 1} Target Roadmap`,
  notes: `Target: $${i + 1}/day demo. Strictly 1% risk per trade. Zero revenge trades.`,
}));

const getTodayString = () => new Date().toISOString().split('T')[0];

const TraderLifeContext = createContext<TraderLifeContextType | undefined>(undefined);

export const TraderLifeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentScreen, setCurrentScreen] = useState<Screen>('dashboard');
  const [selectedDate, setSelectedDate] = useState<string>(getTodayString());
  const [toasts, setToasts] = useState<Toast[]>([]);

  // State with LocalStorage persistence
  const [settings, setSettings] = useState<UserSettings>(() => {
    const saved = localStorage.getItem('mtl_settings');
    return saved ? JSON.parse(saved) : DEFAULT_SETTINGS;
  });

  const [trades, setTrades] = useState<Trade[]>(() => {
    const saved = localStorage.getItem('mtl_trades');
    if (saved) return JSON.parse(saved);
    const today = getTodayString();
    return [
      {
        id: 'trade-1',
        date: today,
        time: '09:30',
        pair: 'EUR/USD',
        direction: 'BUY',
        entryPrice: 1.0825,
        stopLoss: 1.0815,
        takeProfit: 1.0855,
        riskPercent: 1.0,
        riskAmount: 100,
        result: 'WIN',
        pnl: 300,
        setup: 'Liquidity Sweep + Order Block',
        reason: 'Asian high swept, strong 15m displacement upward, entered on 5m FVG',
        emotionBefore: 'Calm',
        emotionDuring: 'Disciplined',
        accordingToPlan: true,
        fomo: false,
        revenge: false,
        overtrade: false,
        mistake: 'None',
        lesson: 'Patience paid off. Waited for confirmation instead of anticipating.',
        createdAt: Date.now() - 3600000 * 4,
      },
      {
        id: 'trade-2',
        date: today,
        time: '11:15',
        pair: 'XAU/USD',
        direction: 'SELL',
        entryPrice: 2655.4,
        stopLoss: 2661.0,
        takeProfit: 2640.0,
        riskPercent: 1.0,
        riskAmount: 100,
        result: 'LOSS',
        pnl: -100,
        setup: 'Breakout Retest',
        reason: 'Trendline break retest during London open',
        emotionBefore: 'Confident',
        emotionDuring: 'Relaxed',
        accordingToPlan: true,
        fomo: false,
        revenge: false,
        overtrade: false,
        mistake: 'None',
        lesson: 'Loss is normal cost of doing business. SL held perfectly according to plan.',
        createdAt: Date.now() - 3600000 * 2,
      },
    ];
  });

  const [salahRecords, setSalahRecords] = useState<Record<string, SalahRecord>>(() => {
    const saved = localStorage.getItem('mtl_salah');
    if (saved) return JSON.parse(saved);
    const today = getTodayString();
    return {
      [today]: {
        date: today,
        fajr: 'JAMAAT',
        dhuhr: 'JAMAAT',
        asr: 'NONE',
        maghrib: 'NONE',
        isha: 'NONE',
        notes: 'Stay focused on discipline and gratitude.',
      },
    };
  });

  const [habits, setHabits] = useState<Habit[]>(() => {
    const saved = localStorage.getItem('mtl_habits');
    return saved ? JSON.parse(saved) : DEFAULT_HABITS;
  });

  const [habitLogs, setHabitLogs] = useState<Record<string, string[]>>(() => {
    const saved = localStorage.getItem('mtl_habit_logs');
    if (saved) return JSON.parse(saved);
    const today = getTodayString();
    return {
      [today]: ['1', '2', '3'],
    };
  });

  const [psychologyLogs, setPsychologyLogs] = useState<PsychologyLog[]>(() => {
    const saved = localStorage.getItem('mtl_psychology');
    if (saved) return JSON.parse(saved);
    const today = getTodayString();
    return [
      {
        id: 'psy-1',
        date: today,
        time: '08:45',
        calm: 5,
        fear: 1,
        greed: 1,
        fomo: 1,
        revenge: 1,
        confidence: 5,
        stress: 2,
        note: 'Pre-market check: Slept well, prayed Fajr in Jamaat, mind is calm and ready to execute only valid setups.',
        sessionType: 'Pre-Market',
        createdAt: Date.now() - 3600000 * 5,
      },
    ];
  });

  const [goals, setGoals] = useState<MonthlyGoal[]>(() => {
    const saved = localStorage.getItem('mtl_goals');
    return saved ? JSON.parse(saved) : DEFAULT_GOALS;
  });

  const [dailyReviews, setDailyReviews] = useState<Record<string, DailyReview>>(() => {
    const saved = localStorage.getItem('mtl_reviews');
    if (saved) return JSON.parse(saved);
    const today = getTodayString();
    return {
      [today]: {
        date: today,
        didWell: 'Followed 1% risk rule on both demo trades. Did not overtrade.',
        biggestMistake: 'Slight impatience before the London session open.',
        mistakeReason: 'Checked chart too frequently before NY bell.',
        improvement: 'Set price alerts and walk away from chart until alert triggers.',
        lesson: 'Trading is 80% waiting and 20% execution.',
        disciplineScore: 9,
        updatedAt: Date.now(),
      },
    };
  });

  const [isLocked, setIsLocked] = useState<boolean>(() => {
    return settings.pinLockEnabled;
  });

  // Sync to localStorage
  useEffect(() => {
    localStorage.setItem('mtl_settings', JSON.stringify(settings));
  }, [settings]);

  useEffect(() => {
    localStorage.setItem('mtl_trades', JSON.stringify(trades));
  }, [trades]);

  useEffect(() => {
    localStorage.setItem('mtl_salah', JSON.stringify(salahRecords));
  }, [salahRecords]);

  useEffect(() => {
    localStorage.setItem('mtl_habits', JSON.stringify(habits));
  }, [habits]);

  useEffect(() => {
    localStorage.setItem('mtl_habit_logs', JSON.stringify(habitLogs));
  }, [habitLogs]);

  useEffect(() => {
    localStorage.setItem('mtl_psychology', JSON.stringify(psychologyLogs));
  }, [psychologyLogs]);

  useEffect(() => {
    localStorage.setItem('mtl_goals', JSON.stringify(goals));
  }, [goals]);

  useEffect(() => {
    localStorage.setItem('mtl_reviews', JSON.stringify(dailyReviews));
  }, [dailyReviews]);

  // Toast notifications
  const showToast = (message: string, type: 'success' | 'error' | 'info' = 'success') => {
    const id = Math.random().toString(36).substring(2, 9);
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => {
      removeToast(id);
    }, 4000);
  };

  const removeToast = (id: string) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  const updateSettings = (newSettings: Partial<UserSettings>) => {
    setSettings((prev) => ({ ...prev, ...newSettings }));
    showToast('Settings saved successfully!');
  };

  const addTrade = (tradeData: Omit<Trade, 'id' | 'createdAt'>) => {
    const newTrade: Trade = {
      ...tradeData,
      id: 'trade-' + Date.now(),
      createdAt: Date.now(),
    };
    setTrades((prev) => [newTrade, ...prev]);
    showToast('Trade logged to journal!');
  };

  const updateTrade = (trade: Trade) => {
    setTrades((prev) => prev.map((t) => (t.id === trade.id ? trade : t)));
    showToast('Trade updated successfully!');
  };

  const deleteTrade = (id: string) => {
    setTrades((prev) => prev.filter((t) => t.id !== id));
    showToast('Trade deleted from journal.', 'info');
  };

  const updateSalahStatus = (date: string, prayer: keyof Omit<SalahRecord, 'date' | 'notes'>, status: SalahRecord['fajr']) => {
    setSalahRecords((prev) => {
      const existing = prev[date] || {
        date,
        fajr: 'NONE',
        dhuhr: 'NONE',
        asr: 'NONE',
        maghrib: 'NONE',
        isha: 'NONE',
      };
      return {
        ...prev,
        [date]: {
          ...existing,
          [prayer]: status,
        },
      };
    });
    showToast(`${prayer.toUpperCase()} marked as ${status}`);
  };

  const toggleHabit = (habitId: string, date: string = selectedDate) => {
    setHabitLogs((prev) => {
      const currentList = prev[date] || [];
      const isCompleted = currentList.includes(habitId);
      const updatedList = isCompleted
        ? currentList.filter((id) => id !== habitId)
        : [...currentList, habitId];
      return { ...prev, [date]: updatedList };
    });
  };

  const addCustomHabit = (titleEn: string, titleBn: string, category: string, icon: string) => {
    const newHabit: Habit = {
      id: 'habit-' + Date.now(),
      titleEn,
      titleBn: titleBn || titleEn,
      category,
      icon,
      order: habits.length + 1,
    };
    setHabits((prev) => [...prev, newHabit]);
    showToast('New habit added!');
  };

  const deleteHabit = (id: string) => {
    setHabits((prev) => prev.filter((h) => h.id !== id));
    showToast('Habit removed.', 'info');
  };

  const addPsychologyLog = (logData: Omit<PsychologyLog, 'id' | 'createdAt'>) => {
    const newLog: PsychologyLog = {
      ...logData,
      id: 'psy-' + Date.now(),
      createdAt: Date.now(),
    };
    setPsychologyLogs((prev) => [newLog, ...prev]);
    showToast('Psychology reflection saved!');
  };

  const updateGoal = (month: number, target: number, notes?: string) => {
    setGoals((prev) =>
      prev.map((g) =>
        g.month === month ? { ...g, targetPerDay: target, notes: notes !== undefined ? notes : g.notes } : g
      )
    );
    showToast(`Month ${month} goal updated!`);
  };

  const saveDailyReview = (review: DailyReview) => {
    setDailyReviews((prev) => ({
      ...prev,
      [review.date]: { ...review, updatedAt: Date.now() },
    }));
    showToast('Daily review saved!');
  };

  const unlockApp = (pin: string) => {
    if (pin === settings.pinCode || !settings.pinLockEnabled) {
      setIsLocked(false);
      return true;
    }
    showToast('Incorrect PIN code', 'error');
    return false;
  };

  const lockApp = () => {
    if (settings.pinLockEnabled) {
      setIsLocked(true);
    }
  };

  const exportData = () => {
    const data = {
      settings,
      trades,
      salahRecords,
      habits,
      habitLogs,
      psychologyLogs,
      goals,
      dailyReviews,
      exportDate: new Date().toISOString(),
      version: '1.0.0',
    };
    return JSON.stringify(data, null, 2);
  };

  const importData = (jsonData: string): boolean => {
    try {
      const data = JSON.parse(jsonData);
      if (data.settings) setSettings(data.settings);
      if (data.trades) setTrades(data.trades);
      if (data.salahRecords) setSalahRecords(data.salahRecords);
      if (data.habits) setHabits(data.habits);
      if (data.habitLogs) setHabitLogs(data.habitLogs);
      if (data.psychologyLogs) setPsychologyLogs(data.psychologyLogs);
      if (data.goals) setGoals(data.goals);
      if (data.dailyReviews) setDailyReviews(data.dailyReviews);
      showToast('All journal and tracking data imported successfully!');
      return true;
    } catch {
      showToast('Invalid backup JSON file.', 'error');
      return false;
    }
  };

  const resetAllData = () => {
    localStorage.clear();
    setSettings(DEFAULT_SETTINGS);
    setTrades([]);
    setSalahRecords({});
    setHabits(DEFAULT_HABITS);
    setHabitLogs({});
    setPsychologyLogs([]);
    setGoals(DEFAULT_GOALS);
    setDailyReviews({});
    showToast('All data has been reset to defaults.', 'info');
  };

  return (
    <TraderLifeContext.Provider
      value={{
        currentScreen,
        setCurrentScreen,
        selectedDate,
        setSelectedDate,
        language: settings.language,
        setLanguage: (lang) => updateSettings({ language: lang }),
        settings,
        updateSettings,
        trades,
        addTrade,
        updateTrade,
        deleteTrade,
        salahRecords,
        updateSalahStatus,
        habits,
        habitLogs,
        toggleHabit,
        addCustomHabit,
        deleteHabit,
        psychologyLogs,
        addPsychologyLog,
        goals,
        updateGoal,
        dailyReviews,
        saveDailyReview,
        isLocked,
        unlockApp,
        lockApp,
        toasts,
        showToast,
        removeToast,
        exportData,
        importData,
        resetAllData,
      }}
    >
      {children}
    </TraderLifeContext.Provider>
  );
};

export const useTraderLife = () => {
  const context = useContext(TraderLifeContext);
  if (!context) {
    throw new Error('useTraderLife must be used within a TraderLifeProvider');
  }
  return context;
};
