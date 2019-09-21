package teampro;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.*;

//공유자원 
class Resource
{
	static Integer time=0; //실제 시간
	static Queue ReadyQueue = new Queue(); //작업준비 큐
	static Queue CompletedQueue = new Queue(); //작업완료 큐
	static boolean preempted = false; //선점유무
	static PCB currentPCB; //현재작업중인 프로세스정보
	static boolean finished = false; //작업종료유무	
	
	static int temp_time =0;
	static int cnt = 0 ;
	static PCB temp_pcb =null; 
	
	
}

//작업큐 정의 (연결리스트 상속) : PCB 데이터를 저장함. 정렬비교를 위해 key값 비교를함.
class Queue extends LinkedList<PCB>
{
	public boolean equals(LinkedList<PCB> obj)
	{
		if(obj.size() != this.size())
		{
			return false;
		}
		for(int i=0; i<this.size(); i++)
		{
			if(!obj.get(i).equals(this.get(i)))
			{
				return false;
			}
		}
		return true;
	}
}

//PCB 자료 정의
class PCB implements Comparable<PCB>
{
	int startTime; //프로세스 최근작업 시각
	int endTime; //프로세스 작업 완료시각.
	int arrive; //도착시각
	int id; //프로세스 id
	int pri; //우선순위
	int bust; //실행시간
	int remain; //남은실행시간
	int waittime; //대기시간
	double key; //정렬을 위한 키(우선순위 or 실행시간 or id or 남은실행시간)
	
	PCB(int arrive, int id, int pri, int bust)//PCB 초기화를 위한 생성자
	{
		this.arrive = arrive; 
		this.id = id; this.pri = pri; this.bust = bust; this.remain = bust;
	}
	
	//객체 크기 비교시 key으로 비교
	public int compareTo(PCB ob)
	{
		double targetKey = ob.key;
		if(key == targetKey) return 0;
		else if(key > targetKey) return 1;
		else return -1;
	}
	
	//id로 같은 객체인지 확인
	public boolean equals(PCB ob) 
	{
		if(ob.id != this.id) 
			return false;
		return true;
	}
}

//정해진 단위시간(msec)으로 시간을 증가
class WorkTimer extends Thread
{
	int msec;
	WorkTimer()  //기본단위시간
	{
		this.msec = 100;
	}
	WorkTimer(int msec) //설정 단위시간
	{
		this.msec = msec;
	}
	public void run() //타이머스래드
	{
		Resource.finished = false;
		//작업프로세스 설계
		PCB p1 = new PCB(0, 1, 3,10);
		PCB p2 = new PCB(1, 2, 2,28);
		PCB p3 = new PCB(2, 3, 4, 6);
		PCB p4 = new PCB(3, 4, 1, 4);
		PCB p5 = new PCB(4, 5, 2,14);
		
		//작업완료시간 계산
		int endtime = p1.bust + p2.bust + p3.bust + p4.bust + p5.bust;
		while(endtime >= Resource.time)//모든 작업이 끝날때까지 타이머스래드 작동
		{
			try {
				
				//각 도착시간에 맞는 프로세스를 EnQueue
				if(Resource.time == p1.arrive)
				{
					synchronized(Resource.ReadyQueue)//상호배제(모니터)
					{
						Resource.ReadyQueue.offer(p1);
					}
				}
				if(Resource.time == p2.arrive)
				{
					synchronized(Resource.ReadyQueue)
					{
						Resource.ReadyQueue.offer(p2);
					}
				}
				if(Resource.time == p3.arrive)
				{
					synchronized(Resource.ReadyQueue)
					{
						Resource.ReadyQueue.offer(p3);
					}
				}
				if(Resource.time == p4.arrive)
				{
					synchronized(Resource.ReadyQueue)
					{
						Resource.ReadyQueue.offer(p4);
					}
				}
				if(Resource.time == p5.arrive)
				{
					synchronized(Resource.ReadyQueue)
					{
						Resource.ReadyQueue.offer(p5);
					}
				}
				Thread.sleep(msec);
				Resource.time++; 
				
				//큐에 대기중인 프로세스는 시간증가에 맞춰서 대기시간 증가
				synchronized(Resource.ReadyQueue)
				{
					for(PCB p : Resource.ReadyQueue)
					{
						p.waittime++;
					}
				}
				
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		Resource.finished = true;
		for(PCB p : Resource.CompletedQueue)
		{
			p.key = p.id;
		}
		
		Resource.CompletedQueue.sort(Comparator.naturalOrder());
		System.out.println();
		
		//각 프로세스의 반환시간, 대기시간 그리고 평균반환시간, 평균 대기시간 계산
		double totalwait = 0, totalturn = 0; int size = Resource.CompletedQueue.size(); 
		for(PCB p : Resource.CompletedQueue)
		{
			System.out.println("p" + p.id + ")     wait:" + p.waittime + 
					                          "\treturn:" + (p.endTime-p.arrive));
			
			totalwait += p.waittime; //대기시간합산
			totalturn += p.endTime-p.arrive; //반환시간 합산
		}
		System.out.println("avg wait:" + (totalwait/size) + "\tavg return:" + (totalturn/size));
	}
}

//CPU 스캐줄러
class CPU_Scheduler extends Thread
{
	//스케쥴방식 정의
	final static int FCFS = 0;
	final static int SJF = 1;
	final static int PRIORITY = 2;
	final static int HRN = 3;
	final static int RR = 4;
	
	int type = FCFS; //스케줄러가 선택한 스케쥴기법
	boolean PreemptionMode = false; //선점 유무
	// FCFS와 HRN은 이값의 영향없이 무조건 비선점으로 진행
	
	
	CPU_Scheduler(){ //기본스케줄러 (FCFS 방식사용)
		type = FCFS;
	}
	
	CPU_Scheduler(int type, boolean PreemtionMode)
	{
		this.type = type;
		this.PreemptionMode = PreemtionMode;
	}
	
	//SJF를 사용할경우 Queue정렬 방식을 기술
	void SortSJF() 
	{
		
		synchronized(Resource.ReadyQueue)
		{
			for(PCB p : Resource.ReadyQueue)
			{
				p.key = p.bust;
			}
			Resource.ReadyQueue.sort(Comparator.naturalOrder());
		}
	
		//선점 SJF인 경우
		if(PreemptionMode)
		{
			
			synchronized(Resource.ReadyQueue)
			{
				if(Resource.ReadyQueue.peek() != null && Resource.currentPCB != null)
				{
					if(Resource.currentPCB.remain > Resource.ReadyQueue.peek().remain)
					{
						Resource.preempted = true;
						for(PCB p : Resource.ReadyQueue)
						{
							p.key = p.remain;
						}
						Resource.ReadyQueue.sort(Comparator.naturalOrder());
					}
				}
			}
		}
	
	}
	//우선순위 방식
	void SortPRI() 
	{
		synchronized(Resource.ReadyQueue)
		{
			for(PCB p : Resource.ReadyQueue)
			{
				p.key = p.pri;
			}
			Resource.ReadyQueue.sort(Comparator.reverseOrder());
		}
		if(PreemptionMode)
		{
			synchronized(Resource.ReadyQueue)
			{
				if(Resource.ReadyQueue.peek() != null && Resource.currentPCB != null)
				{
					if(Resource.currentPCB.pri < Resource.ReadyQueue.peek().pri)
					{
						Resource.preempted = true;
						for(PCB p : Resource.ReadyQueue)
						{
							p.key = p.pri;
						}
						Resource.ReadyQueue.sort(Comparator.reverseOrder());
					}
				}
			}
		}
	}
	//HRN 방식
	void SortHRN() 
	{		
		synchronized(Resource.ReadyQueue)
		{
			for(PCB p : Resource.ReadyQueue)
			{
				int a = p.bust + p.waittime;
				int pri = a / p.bust;
				p.key = pri;
			}
			Resource.ReadyQueue.sort(Comparator.reverseOrder());
		}
		
	}
	
	void SortRR()
	{
		synchronized(Resource.ReadyQueue)
		
		{	
			if(Resource.currentPCB != null && Resource.temp_time != Resource.time)
			{		       
				   
				if(Resource.temp_pcb != null && Resource.currentPCB != null)
				{
				   if(!Resource.temp_pcb.equals(Resource.currentPCB))
				     {
					   Resource.cnt=0;
					   Resource.temp_pcb = Resource.currentPCB;
					   
				      }
				}
				    Resource.cnt++;
			        		       
				if(Resource.currentPCB.remain!=0){
					
			       if(Resource.cnt==5 && Resource.currentPCB.remain>=5)
			       {			  
			    	   Resource.cnt=0;
			    	   Resource.preempted = true;
			    	   
			    	   
			       }else if(Resource.currentPCB.remain<5 && Resource.cnt==1)
			       {
			    	   
			    	   Resource.temp_pcb = Resource.currentPCB;
			    	   
			       }
			    	  
				     Resource.temp_time=Resource.time;		
				}    
			  }
			}
			
		}
	
	
	//큐 정렬
	public void run()
	{
		Queue copyQueue = (Queue) Resource.ReadyQueue.clone();
		
		int time = Resource.time;// 현재시간저장
		
		while(!Resource.finished)
		{
			try 
			{
				synchronized(Resource.ReadyQueue) {
					//HRN은 시간이 지날때마다 key값을 변경
					if(type == HRN && time != Resource.time)
					{
						time = Resource.time;
						SortHRN(); 
					}
					
					//HRN과 FCFS방식을 제외한 나머지 방식은 준비큐에 변화가 있을때마다 정렬시도
					else if(!copyQueue.equals(Resource.ReadyQueue) && type != FCFS && type !=RR)
					{
						copyQueue = (Queue) Resource.ReadyQueue.clone();
						switch(type)
						{
						case SJF : SortSJF(); break;
						case PRIORITY : SortPRI(); break;
						case RR : SortRR(); break;
						}
						
					}
					else if(type == RR )
					{     

						
						switch(type)
						{
						case RR : SortRR(); break;
						
						}
						
						
						
					}
					
				}
				
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
}


//Processor 스래드
class MyProcessor extends Thread
{
	public void run()
	{
		System.out.print("0"); //간트차트의 시작시간
		while(!Resource.finished)
		{
			try 
			{
				synchronized (Resource.ReadyQueue)
				{
					Resource.currentPCB = Resource.ReadyQueue.poll();
				}
				
				if(Resource.currentPCB != null)
				{ 
					synchronized (Resource.currentPCB)
					{
						//작업중인 PCB의 최근작업시간 저장
						Resource.currentPCB.startTime = Resource.time;
					}
					
					//프로세스 처리.
					while(true)
					{
						int runtime;
						synchronized (Resource.currentPCB)
						{
							//현재 실행시간 계산
							runtime = Resource.time - Resource.currentPCB.startTime;
						}
						
						if(Resource.currentPCB.remain == runtime || Resource.preempted)
						{
							
							System.out.print("---p" + Resource.currentPCB.id 
									+ "---" + Resource.time) ;
							
							//선점체크를 해제
							Resource.preempted = false;
							
							//처리시간만큼 남은실행시간 감소
							synchronized (Resource.currentPCB)
							{
								Resource.currentPCB.remain -= runtime;
							}
							
							if(Resource.currentPCB.remain > 0)
							{
	
								synchronized(Resource.ReadyQueue)
								{
									// 현재 작업프로세스의 임시공간에 저장.
									PCB t = Resource.currentPCB;
									Resource.currentPCB = null;
									//처리가 덜된 프로세스 준비큐에 입장.
									Resource.ReadyQueue.offer(t);
								}
								
							}
							else
							{
								//작업종료시간 기록
								Resource.currentPCB.endTime = Resource.time;
								//작업완료큐에 입장.
								Resource.CompletedQueue.offer(Resource.currentPCB);
								Resource.currentPCB = null;
							}
							
							break;
						}
						Thread.sleep(10);
					}
				}
				Thread.sleep(10);		
			} 
			catch (InterruptedException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

public class CPUScheduler{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		CPU_Scheduler sche = new CPU_Scheduler(CPU_Scheduler.RR, false);
		MyProcessor process = new MyProcessor();
		WorkTimer timer = new WorkTimer(50);
		//작업시작
		sche.start();
		process.start();
		timer.start();
	}

}
