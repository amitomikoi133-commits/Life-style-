import React, { useState } from 'react';
import { TraderLifeProvider, useTraderLife } from './context/TraderLifeContext';
import { Sidebar } from './components/Sidebar';
import { Navbar } from './components/Navbar';
import { BottomNav } from './components/BottomNav';
import { PinLockModal } from './components/PinLockModal';
import { ToastContainer } from './components/ToastContainer';

// Pages
import { Dashboard } from './pages/Dashboard';
import { Journal } from './pages/Journal';
import { RiskManager } from './pages/RiskManager';
import { Psychology } from './pages/Psychology';
import { Routine } from './pages/Routine';
import { SalahTracker } from './pages/SalahTracker';
import { Goals } from './pages/Goals';
import { Analytics } from './pages/Analytics';
import { DailyReview } from './pages/DailyReview';
import { Settings } from './pages/Settings';

const MainContent: React.FC = () => {
  const { currentScreen } = useTraderLife();
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const renderScreen = () => {
    switch (currentScreen) {
      case 'dashboard':
        return <Dashboard />;
      case 'journal':
        return <Journal />;
      case 'risk-manager':
        return <RiskManager />;
      case 'psychology':
        return <Psychology />;
      case 'routine':
        return <Routine />;
      case 'salah':
        return <SalahTracker />;
      case 'goals':
        return <Goals />;
      case 'analytics':
        return <Analytics />;
      case 'daily-review':
        return <DailyReview />;
      case 'settings':
        return <Settings />;
      default:
        return <Dashboard />;
    }
  };

  return (
    <div className="min-h-screen bg-[#07090D] text-slate-100 flex flex-col">
      {/* Sidebar */}
      <Sidebar isOpen={sidebarOpen} onClose={() => setSidebarOpen(false)} />

      {/* Main Container Area (with offset on desktop for fixed sidebar) */}
      <div className="lg:pl-72 flex flex-col flex-1 min-h-screen">
        <Navbar onOpenSidebar={() => setSidebarOpen(true)} />

        <main className="flex-1 p-4 sm:p-6 lg:p-8 max-w-7xl w-full mx-auto">
          {renderScreen()}
        </main>

        <BottomNav />
      </div>

      {/* Overlays */}
      <PinLockModal />
      <ToastContainer />
    </div>
  );
};

export const App: React.FC = () => {
  return (
    <TraderLifeProvider>
      <MainContent />
    </TraderLifeProvider>
  );
};

export default App;
