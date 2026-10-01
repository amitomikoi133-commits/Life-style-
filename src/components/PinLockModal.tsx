import React, { useState } from 'react';
import { Lock, KeyRound, ShieldAlert } from 'lucide-react';
import { useTraderLife } from '../context/TraderLifeContext';

export const PinLockModal: React.FC = () => {
  const { isLocked, unlockApp, settings } = useTraderLife();
  const [pin, setPin] = useState('');
  const [error, setError] = useState(false);

  if (!isLocked || !settings.pinLockEnabled) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const success = unlockApp(pin);
    if (!success) {
      setError(true);
      setPin('');
    } else {
      setError(false);
    }
  };

  const handleKeypad = (num: string) => {
    if (pin.length < 6) {
      const newPin = pin + num;
      setPin(newPin);
      if (newPin.length === settings.pinCode.length) {
        const success = unlockApp(newPin);
        if (!success) {
          setError(true);
          setPin('');
        }
      }
    }
  };

  const handleBackspace = () => {
    setPin((prev) => prev.slice(0, -1));
  };

  return (
    <div className="fixed inset-0 z-50 bg-[#07090D] flex items-center justify-center p-4">
      <div className="w-full max-w-sm p-6 rounded-3xl bg-[#0B0E14] border border-[#1E2534] shadow-2xl text-center space-y-6">
        <div className="w-16 h-16 mx-auto rounded-2xl bg-amber-500/15 border border-amber-500/30 flex items-center justify-center text-amber-400">
          <Lock className="w-8 h-8" />
        </div>

        <div>
          <h2 className="text-xl font-extrabold text-slate-100">Journal Protected</h2>
          <p className="text-xs text-slate-400 mt-1">Enter your Security PIN to unlock private tracking</p>
        </div>

        {/* PIN Dots */}
        <div className="flex justify-center gap-3">
          {Array.from({ length: settings.pinCode.length || 4 }).map((_, idx) => (
            <div
              key={idx}
              className={`w-4 h-4 rounded-full border transition-all ${
                idx < pin.length
                  ? 'bg-amber-400 border-amber-400 shadow-md shadow-amber-400/30'
                  : 'bg-[#1E2534] border-[#2A3346]'
              }`}
            />
          ))}
        </div>

        {error && (
          <p className="text-xs font-semibold text-rose-400 flex items-center justify-center gap-1">
            <ShieldAlert className="w-3.5 h-3.5" /> Incorrect PIN. Please try again.
          </p>
        )}

        {/* Number Keypad */}
        <div className="grid grid-cols-3 gap-3 pt-2">
          {['1', '2', '3', '4', '5', '6', '7', '8', '9', 'C', '0', '⌫'].map((k) => (
            <button
              key={k}
              type="button"
              onClick={() => {
                if (k === 'C') setPin('');
                else if (k === '⌫') handleBackspace();
                else handleKeypad(k);
              }}
              className="py-3.5 rounded-2xl bg-[#10141C] hover:bg-[#1E2534] border border-[#1E2534] active:scale-95 text-slate-100 text-lg font-bold transition-all"
            >
              {k}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
};
