package utils;

import java.util.TimerTask;

import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import calculator.Calculator;

public class CPUTask extends TimerTask {
	
	private static final int TOTAL_TIME_UPDATE_LIMIT = 2000;

    private final Sigar sigar;
    private final int cpuCount;
    private final long pid;
    private ProcCpu prevPc;
	
	public CPUTask() throws SigarException {
		sigar = new Sigar();
        cpuCount = sigar.getCpuList().length;
        pid = sigar.getPid();
        prevPc = sigar.getProcCpu(pid);
	}

	@Override
	public void run() {
		try {
            ProcCpu curPc = sigar.getProcCpu(pid);
            long totalDelta = curPc.getTotal() - prevPc.getTotal();
            long timeDelta = curPc.getLastTime() - prevPc.getLastTime();
            if (totalDelta == 0) {
                if (timeDelta > TOTAL_TIME_UPDATE_LIMIT) Calculator.load = 0;
                if (Calculator.load == 0) prevPc = curPc;
            } else {
            	Calculator.load = 100. * totalDelta / timeDelta / cpuCount;
                prevPc = curPc;
            }
	    System.out.println("Load :"+Calculator.load);
        } catch (SigarException ex) {
            throw new RuntimeException(ex);
        }
	}

}
