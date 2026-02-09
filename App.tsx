import React, { useState, useEffect } from 'react';

/**
 * PRODUCTION-GRADE SIMULATION UI
 * This simulates the Android application's UI and behavior for preview purposes.
 */
export default function App() {
  const [view, setView] = useState('Mock');
  const [status, setStatus] = useState('Disconnected');
  const [isScanning, setIsScanning] = useState(false);
  const [scannedDevices, setScannedDevices] = useState([]);
  const [connectedDevice, setConnectedDevice] = useState(null);
  const [isRecording, setIsRecording] = useState(false);
  const [audioReady, setAudioReady] = useState(false);
  const [motorMode, setMotorMode] = useState('Fast');
  const [sendingProgress, setSendingProgress] = useState(null);

  const files = [
    { name: 'BleManager.kt', path: 'ble/BleManager.kt', type: 'Bluetooth' },
    { name: 'BlePacket.kt', path: 'ble/BlePacket.kt', type: 'Protocol' },
    { name: 'AudioRecorder.kt', path: 'audio/AudioRecorder.kt', type: 'Audio' },
    { name: 'AudioConverter.kt', path: 'audio/AudioConverter.kt', type: 'Audio' },
    { name: 'AmplitudeExtractor.kt', path: 'audio/AmplitudeExtractor.kt', type: 'DSP' },
    { name: 'MainViewModel.kt', path: 'ui/MainViewModel.kt', type: 'Architecture' },
    { name: 'MainScreen.kt', path: 'ui/MainScreen.kt', type: 'UI' },
    { name: 'AndroidManifest.xml', path: 'AndroidManifest.xml', type: 'Config' }
  ];

  const simulateScan = () => {
    if (isScanning) return;
    setIsScanning(true);
    setScannedDevices([]);
    setStatus('Scanning for BLE peripherals...');
    
    setTimeout(() => setScannedDevices(prev => [...prev, {name: 'ESP32_AUDIO_MTR', addr: 'C4:4F:33:12:AB:09'}]), 600);
    setTimeout(() => setScannedDevices(prev => [...prev, {name: 'BLE_SMART_SYNC', addr: 'EE:21:99:FF:00:11'}]), 1400);
    setTimeout(() => setScannedDevices(prev => [...prev, {name: 'DEVELOP_TARGET_01', addr: '00:11:22:33:44:55'}]), 2500);

    setTimeout(() => {
      setIsScanning(false);
      setStatus('Scan finished. Choose a device.');
    }, 4500);
  };

  const simulateConnect = (name) => {
    setStatus(`Connecting to ${name}...`);
    setTimeout(() => {
      setConnectedDevice(name);
      setStatus(`Connected: ${name} (MTU 247)`);
    }, 1500);
  };

  const toggleRecording = () => {
    if (isRecording) {
      setIsRecording(false);
      setAudioReady(true);
      setStatus('Mic Recording Ready (8kHz PCM)');
    } else {
      setIsRecording(true);
      setAudioReady(false);
      setStatus('Recording Audio from Mic...');
    }
  };

  const simulateSend = () => {
    if (!connectedDevice || !audioReady || sendingProgress !== null) return;
    setSendingProgress(0);
    setStatus('Streaming Data: Packet 0x01 (Audio)');
    
    let prog = 0;
    const interval = setInterval(() => {
      prog += 4;
      setSendingProgress(prog);
      
      if (prog === 40) setStatus('Streaming Data: Packet 0x02 (Brightness)');
      if (prog === 80) setStatus('Streaming Data: Packet 0x03 (Motor)');
      
      if (prog >= 100) {
        clearInterval(interval);
        setSendingProgress(null);
        setStatus('Transmission Complete (0xFF)');
      }
    }, 120);
  };

  return (
    <div className="h-screen w-screen bg-gray-950 flex flex-col font-sans text-gray-200 overflow-hidden">
      <header className="p-4 border-b border-gray-800 bg-gray-900 flex justify-between items-center z-50 shadow-md shrink-0">
        <div className="flex items-center gap-6">
          <div className="flex items-center gap-2">
             <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center shadow-lg shadow-blue-900/20">
                <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M9 19V6l12-3v13M9 19c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zm12-3c0 1.105-1.343 2-3 2s-3-.895-3-2 1.343-2 3-2 3 .895 3 2zM9 10l12-3" /></svg>
             </div>
             <h1 className="text-xl font-black bg-gradient-to-r from-blue-400 to-emerald-400 bg-clip-text text-transparent">
               BLE Audio Tool
             </h1>
          </div>
          <nav className="flex bg-gray-800 rounded-full p-1 text-[10px] font-bold uppercase tracking-widest">
            <button 
              onClick={() => setView('Mock')} 
              className={`px-6 py-2 rounded-full transition-all ${view === 'Mock' ? 'bg-blue-600 text-white shadow-md' : 'text-gray-400 hover:text-gray-200'}`}
            >
              Application
            </button>
            <button 
              onClick={() => setView('Source')} 
              className={`px-6 py-2 rounded-full transition-all ${view === 'Source' ? 'bg-blue-600 text-white shadow-md' : 'text-gray-400 hover:text-gray-200'}`}
            >
              Source Files
            </button>
          </nav>
        </div>
      </header>

      <main className="flex-1 flex overflow-hidden relative">
        {view === 'Mock' ? (
          <div className="flex-1 flex items-center justify-center bg-[#050505] relative p-6 overflow-hidden">
            <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-blue-500/5 blur-[120px] rounded-full pointer-events-none"></div>

            {/* Mobile Device Frame */}
            <div className="w-[340px] h-[680px] max-h-full bg-[#0c0c0c] rounded-[3.5rem] border-[10px] border-gray-900 shadow-[0_40px_100px_-20px_rgba(0,0,0,0.8)] relative flex flex-col overflow-hidden ring-1 ring-gray-800/50">
              
              {/* Notch */}
              <div className="absolute top-0 left-1/2 -translate-x-1/2 w-28 h-7 bg-gray-900 rounded-b-3xl z-30 flex items-center justify-center gap-2">
                <div className="w-10 h-1 bg-gray-800 rounded-full"></div>
                <div className="w-2 h-2 bg-gray-800 rounded-full"></div>
              </div>
              
              <div className="h-10 flex items-center justify-between px-8 pt-4 text-[11px] text-gray-500 font-bold tracking-tighter shrink-0">
                <span>14:32</span>
                <div className="flex gap-2">
                   <div className="w-3 h-3 bg-gray-800 rounded-full"></div>
                   <div className="w-3 h-3 bg-gray-800 rounded-full"></div>
                </div>
              </div>

              <div className="flex-1 flex flex-col p-5 bg-[#0f1115] overflow-y-auto no-scrollbar">
                {/* System Monitoring Card */}
                <div className={`mb-6 p-4 rounded-2xl border transition-all duration-300 ${isRecording ? 'bg-red-500/10 border-red-500/30' : 'bg-blue-500/5 border-blue-500/20'}`}>
                   <div className="flex items-center justify-between mb-3">
                      <span className="text-[9px] font-black uppercase text-gray-500 tracking-widest">System Status</span>
                      <div className={`w-2 h-2 rounded-full ${connectedDevice ? 'bg-green-500 shadow-[0_0_8px_green]' : 'bg-red-500 animate-pulse'}`}></div>
                   </div>
                   <p className={`text-xs font-mono break-all leading-tight ${isRecording ? 'text-red-400' : 'text-blue-300'}`}>
                     {status}
                   </p>
                </div>

                {/* Device List Section */}
                <div className="flex-1 flex flex-col min-h-0">
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-xs font-black text-gray-400 uppercase tracking-widest">Nearby ESP32</h3>
                    <button 
                      onClick={simulateScan} 
                      disabled={isScanning || isRecording}
                      className="text-[10px] font-bold text-blue-400 hover:text-blue-300 disabled:opacity-30 transition-all flex items-center gap-1"
                    >
                      {isScanning ? 'SCANNING...' : 'SCAN'}
                    </button>
                  </div>
                  
                  <div className="flex-1 space-y-2 overflow-y-auto no-scrollbar pb-4">
                    {scannedDevices.map(d => (
                      <div 
                        key={d.addr}
                        onClick={() => !isRecording && simulateConnect(d.name)}
                        className={`p-3 rounded-2xl border transition-all ${
                          connectedDevice === d.name 
                          ? 'bg-blue-600 border-blue-400 shadow-lg' 
                          : 'bg-gray-900 border-gray-800 hover:border-gray-700 cursor-pointer'
                        } ${isRecording ? 'opacity-40 pointer-events-none' : ''}`}
                      >
                        <div className="flex items-center gap-3">
                           <div className="w-8 h-8 flex items-center justify-center bg-gray-800 rounded-xl text-blue-400">
                              <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 24 24"><path d="M7 17l9.2-9.2L11 2.5l-1 1V11H6.5l-1-1L4.3 11.2l2.7 2.8L4.3 16.8l1.2 1.2 1-1H10v7.5l1-1 5.2-5.3L7 17zm4-12.7l3 3-3 3V4.3zm0 15.4V13.7l3 3-3 3z"/></svg>
                           </div>
                           <div className="flex-1 min-w-0">
                              <p className="text-xs font-bold truncate text-white">{d.name}</p>
                              <p className="text-[9px] font-mono text-gray-500">{d.addr}</p>
                           </div>
                        </div>
                      </div>
                    ))}
                    {isScanning && (
                      <div className="flex justify-center p-4">
                        <div className="w-6 h-6 border-2 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
                      </div>
                    )}
                  </div>
                </div>

                {/* Footer Controls */}
                <div className="pt-4 border-t border-gray-800/50 space-y-4 shrink-0">
                   <div className="grid grid-cols-2 gap-3">
                      <button 
                        onClick={() => { setAudioReady(true); setStatus('Mock File Loaded'); }}
                        disabled={isRecording}
                        className="py-3 bg-gray-900 border border-gray-800 rounded-2xl text-[10px] font-black uppercase tracking-tighter disabled:opacity-30"
                      >
                        File Pick
                      </button>
                      <button 
                        onClick={toggleRecording}
                        className={`py-3 border rounded-2xl text-[10px] font-black uppercase tracking-tighter transition-all ${
                          isRecording 
                          ? 'bg-red-600 border-red-400 text-white shadow-lg shadow-red-900/40' 
                          : 'bg-gray-900 border-gray-800 text-gray-400'
                        }`}
                      >
                        {isRecording ? 'STOP' : 'MIC REC'}
                      </button>
                   </div>

                   <div className="bg-gray-950 p-1 rounded-2xl border border-gray-900 flex">
                      <button 
                        onClick={() => setMotorMode('Fast')}
                        className={`flex-1 py-2 text-[9px] font-black rounded-xl transition-all ${motorMode === 'Fast' ? 'bg-blue-600 text-white shadow-sm' : 'text-gray-600'}`}
                      >
                        FAST
                      </button>
                      <button 
                        onClick={() => setMotorMode('Slow')}
                        className={`flex-1 py-2 text-[9px] font-black rounded-xl transition-all ${motorMode === 'Slow' ? 'bg-blue-600 text-white shadow-sm' : 'text-gray-600'}`}
                      >
                        SLOW
                      </button>
                   </div>

                   <button 
                     onClick={simulateSend}
                     disabled={!connectedDevice || !audioReady || sendingProgress !== null || isRecording}
                     className={`w-full py-4 rounded-3xl text-xs font-black tracking-widest transition-all ${
                       connectedDevice && audioReady && !isRecording 
                       ? 'bg-blue-600 text-white shadow-xl shadow-blue-600/20 active:scale-95' 
                       : 'bg-gray-900 text-gray-700 cursor-not-allowed border border-gray-800'
                     }`}
                   >
                     {sendingProgress !== null ? (
                        <div className="w-full h-1.5 bg-blue-900 rounded-full overflow-hidden">
                           <div className="h-full bg-blue-300 transition-all duration-150" style={{ width: `${sendingProgress}%` }}></div>
                        </div>
                     ) : (
                       'STREAM DATA'
                     )}
                   </button>
                </div>
              </div>
            </div>
          </div>
        ) : (
          <div className="flex-1 bg-gray-900 overflow-y-auto p-12">
            <div className="max-w-4xl mx-auto space-y-8">
               <h2 className="text-3xl font-black text-white uppercase tracking-tighter">Source Blueprint</h2>
               <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {files.map(f => (
                    <div key={f.path} className="p-5 bg-gray-800/30 border border-gray-800 rounded-2xl group hover:border-blue-500/50 transition-all">
                       <span className="text-[10px] font-black text-blue-500 uppercase tracking-widest mb-1 block">{f.type}</span>
                       <p className="text-sm font-bold text-gray-200 group-hover:text-blue-300">{f.name}</p>
                       <p className="text-[10px] text-gray-600 font-mono mt-1">{f.path}</p>
                    </div>
                  ))}
               </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}