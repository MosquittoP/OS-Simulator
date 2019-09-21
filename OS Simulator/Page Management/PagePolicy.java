import java.util.LinkedList;

class FrameTable
{
	Integer frame[][]; //frame[i][0] = 데이터, frame[i][1] = 특수기능 (clock에서는 valid비트, lru에서는 계수기) 
	int capacity; //프레임 총크기
	int cnt; //현재 사용중인 프레임 수
	public FrameTable()
	{
		this.capacity=3;
		frame = new Integer[capacity][2];
		for(int i=0; i<capacity; i++)
		{
			for(int j=0; j<2; j++)
			{
				if(j%2 == 1)
					frame[i][j] = null;
				else
					frame[i][j] = 0;
			}
		}
		framePointer = 0;
	}
	public FrameTable(int capacity)
	{
		//입력받은 capacity로 최대 프레임 할당
		this.capacity=capacity;
		frame = new Integer[capacity][2];
		
		//특기능 부분은  프레임데이터는 NULL로 초기화
		for(int i=0; i<capacity; i++)
		{
			for(int j=0; j<2; j++)
			{
				if(j%2 == 1)
					frame[i][j] = 0;
				else
					frame[i][j] = null;
			}
		}
		framePointer = 0;
	}
	//해당인덱스의 프레임을 가져옴.
	int GetFrameData(int index)
	{
		return frame[index][0];
	}	
	
	//현재 사용중인 프레임 수를 가져옴.
	int GetFrameCount()
	{
		return cnt;
	}
	
	//프레임에서 ref를 찾아 위치를 반환
	int FindIndex(Integer ref)
	{
		for(int i=0; i<capacity; i++)
		{
			if(this.frame[i][0] == ref)
			{
				return i;
			}
		}
		return -1;
	}
	
	//victim이 저장된 프레임을 찾아 data로 교체
	Integer Replace(Integer victim, Integer data)
	{
		
		int index = FindIndex(victim);
		frame[index][0] = data;
		return victim;
	}
	
	//프레임에 데이터 추가
	void Add(int data)
	{
		if(cnt == capacity) 
		{
			System.out.println("프레임이 가득참");
			return;
		}
		frame[cnt++][0] = data;
	}	
	//프레임의 상태를 문자열형태로 반환
	boolean isFull() { return cnt >= capacity;}
	String FrameState() {

		String ret = "";
		for(int i=0; i<cnt; i++)
		{
			ret += frame[i][0] + " ";
		}
		return ret;
	}
	
	/*  Clock용 자원   */
	int framePointer; //프레임포인터
	
	//페이지부재발생 시 참조비트가 0인 프레임을 순서대로 이동하며 찾음
	void SetFramePointer()
	{
		int i = framePointer;
		while(frame[i][1] != 0)
		{
			frame[i][1] = 0;
			i = (i+1)%capacity;
		}
		framePointer = i;	
	}
	
	//페이지Hit발생시 참조된 프레임으로 프레임포인터 이동
	void SetFramePointer(Integer ref)
	{
		int i = framePointer;
		while(frame[i][0] != ref)
		{
			frame[i][1] = 0;
			i = (i+1)%capacity;
		}
		framePointer = i;
		frame[i][1] = 1; //참조비트
	}
	
	//프레임포인터(인덱스) 반환
	int GetFramePointer()
	{
		return framePointer;
	}
	Integer Replace(Integer data)
	{
		Integer victim = frame[framePointer][0]; //희생프레임 저장
		frame[framePointer][0] = data; //프레임을 data로 갱신
		frame[framePointer][1] = 1; //참조비트
		
		//프레임이 가득찬경우가 아니라면
		if(!isFull()) 
		{
			cnt++;
		}
		return victim; //희생프레임반환
	}

	
	/*  LRU용 자원  */
	
	//해당 인덱스의 프레임을 data로 교체하며 교체시간 기록
	Integer Replace(int index, Integer data, Integer time)
	{
		Integer victim = frame[index][0]; //희생프레임 저장
		frame[index][0] = data;
		frame[index][1] = time; //시간기록
		return victim;
	}
	
	//프레임에 data를 추가
	void Add(Integer data, Integer time)
	{
		if(cnt == capacity) {
			System.out.println("프레임이 가득참");
			return;
		}
		frame[cnt][0] = data;
		frame[cnt++][1] = time;
	}
	
	//프레임에 있는 data를 찾아 참조시간 갱신
	void Update(Integer data, Integer time)
	{
		int index = FindIndex(data);
		frame[index][1] = time;
	}
	
	//가장 오래된 프레임을 찾아 반환
	int FindIndex_MinTime(){
		int minIndex = 0;
		for(int i=0; i<cnt; i++)
		{
			if(frame[minIndex][1] > frame[i][1])
			{
				minIndex = i;
			}
		}
		return minIndex;
	}
	
}


/* 대표 페이지 교체 정책 모음 */
class ReplacementPolicy
{
	
	//FIFO 방식 : 최초 참조된 순서대로 희생된 프레임을 결정
	static int FIFO(int[] refSequence, int capacity)
	{
		LinkedList<Integer> queue = new LinkedList<Integer>(); //큐 생성
		FrameTable ft = new FrameTable(capacity); //총용량이 capacity인 프레임생성
		int fcnt = 0; //페이지부재 횟수
		
		for(int i=0; i<refSequence.length; i++)
		{
			int f=0;
			if(ft.FindIndex(refSequence[i]) == -1) //페이지 부재
			{
				f = 1; 
				fcnt++; 
				queue.offer(refSequence[i]); //현재 참조값을 큐에 저장
				
				if(ft.isFull())
				{
					int victim = queue.poll(); //희생 프레임선택
					ft.Replace(victim, refSequence[i]); //프레임 교체
				}
				else // 빈 프레임이 있다면
				{
					ft.Add(refSequence[i]);
				}
			}
		
			System.out.println("f:" + f +" ref:" + refSequence[i] + " frame: " +
					ft.FrameState());
		}
		return fcnt;
	}
	
	//LRU : 참조된지 가장 오래된 프레임을 희생 프레임선택
	static int LRU(int[] refSequence, int capacity)
	{
		// 
		FrameTable ft = new FrameTable(capacity);
		int fcnt = 0;
		for(int i=0; i<refSequence.length; i++)
		{
			int f=0;
			
			
			if(ft.FindIndex(refSequence[i]) == -1)
			{
				f = 1; fcnt++;
				if(ft.isFull())
				{ 
					int victim = ft.FindIndex_MinTime();
					ft.Replace(victim, refSequence[i],i);
				}
				else{
					
					ft.Add(refSequence[i],i);
				}
			}
			else
			{
				// 프레임에서 refSequence[i] 찾아 시간을 갱신
				ft.Update(refSequence[i], i);
			}
			System.out.println("f:" + f +" ref:" + refSequence[i] + " frame: " +
					ft.FrameState());
		}
		return fcnt;
	}
	
	
	//OPT : 참조가  가장 뒤에 있을 프레임을 희생프레임으로 선택
	static int OPT(int[] refSequence, int capacity)
	{
	
		FrameTable ft = new FrameTable(capacity);
		int fcnt=0;
		for(int i=0; i<refSequence.length; i++)
		{
			
			int f=0;
			if(ft.FindIndex(refSequence[i]) == -1)
			{
				f = 1; fcnt++;
				if(ft.isFull())
				{
					
					int index[]= {10,10,10};
					int meet[] = {0,0,0};
					int check =0;
					int co =0;
					int victim =0;
					int fi =0;
					int same =0;
					
				for(int j=i+1;(meet[0]==0||meet[1]==0||meet[2]==0);j++)
					
					
				{
					if(j<20)
					{
						for(int k=0;k<3;k++)
						{
							if((ft.frame[k][0] ==refSequence[j])&&meet[k]==0)
							{
								meet[k]=1;
								index[k] =check;
								check++;
								
							}
							
						}
					}
					else
					{
						
						
						for(int e=0;e<2;e++)
						{
							if(meet[e] ==meet[e+1])
							{
								same +=1;
							}
							if(meet[0] ==meet[2])
							{
								same +=1;
							}
							
						}
						
						if(same ==0)
						{
							for(int b=0;b<3;b++)
							{
								if(meet[b] ==0)
								{
									index[b] =10000;
									meet[b] =1;
								}
							}
						}
						else
						{
							for(j-=1;(meet[0]==0||meet[1]==0||meet[2]==0);j--)
							{
								
								
								for(int k=0;k<3;k++)
								{
									if((ft.frame[k][0] ==refSequence[j])&&meet[k]==0)
									{
										meet[k]=1;
										check++;
										index[k] =check;
										
									}
									
								}
							}
						}
					}
				
					if(meet[0]==1&&meet[1]==1&&meet[2]==1)
					{
						for(int a=1;a<3;a++)
						{
							
							int max=index[0];
							if(max <index[a])
							{
								max =index[a];
								fi=a;
							}
							
							
						}
						
						victim =ft.frame[fi][0];
						ft.Replace(victim, refSequence[i]);
					}
				}
					
					
				}
				els
				{
					ft.Add(refSequence[i]);
				}
			}
			
			// 페이지Hit인 경우엔 작업 없음
			
			System.out.println("f:" + f +" ref:" + refSequence[i] + " frame: " +
			ft.FrameState());
		}
		
		return fcnt;
	}
	
	//CLOCK : 프레임포인터가 가리키고 있는 프레임을 희생프레임으로 선택
	static int CLOCK(int[] refSequence, int capacity)
	{
		FrameTable ft = new FrameTable(capacity);
		int fcnt = 0;
		for(int i=0; i<refSequence.length; i++)
		{
			int f=0;
			if(ft.FindIndex(refSequence[i]) == -1) // 페이지부재
			{
				f = 1; fcnt++;
				ft.SetFramePointer();
				ft.Replace(refSequence[i]);
			}
			else
			{
				ft.SetFramePointer(refSequence[i]);
			}
			
			System.out.println("f:" + f +" ref:" + refSequence[i] + " frame: " +
			ft.FrameState() + "framePointer:" + ft.GetFramePointer());
		}
		return fcnt;
	}
}

public class PagePolicy{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int refSequence[] = {1,2,3,2,1,5,2,1,6,2,5,6,3,1,3,6,1,2,4,3};
		
		System.out.println("OPT >>");
		int optCnt = ReplacementPolicy.OPT(refSequence, 3);
		System.out.println("FIFO >>");
		int fifoCnt = ReplacementPolicy.FIFO(refSequence, 3);
		System.out.println("CLOCK >>");
		int clockCnt = ReplacementPolicy.CLOCK(refSequence, 3);
		System.out.println("LRU >>");
		int lruCnt = ReplacementPolicy.LRU(refSequence, 3);
		
		System.out.println("<페이지 부재 비교>");
		System.out.println("FIFO:" + fifoCnt);
		System.out.println("LRU:" + lruCnt);
		System.out.println("OPT:" + optCnt);
		System.out.println("CLOCK:" + clockCnt);	}

}
