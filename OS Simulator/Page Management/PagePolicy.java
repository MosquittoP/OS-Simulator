import java.util.LinkedList;

class FrameTable
{
	Integer frame[][]; //frame[i][0] = ������, frame[i][1] = Ư����� (clock������ valid��Ʈ, lru������ �����) 
	int capacity; //������ ��ũ��
	int cnt; //���� ������� ������ ��
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
		//�Է¹��� capacity�� �ִ� ������ �Ҵ�
		this.capacity=capacity;
		frame = new Integer[capacity][2];
		
		//Ư��� �κ���  �����ӵ����ʹ� NULL�� �ʱ�ȭ
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
	//�ش��ε����� �������� ������.
	int GetFrameData(int index)
	{
		return frame[index][0];
	}	
	
	//���� ������� ������ ���� ������.
	int GetFrameCount()
	{
		return cnt;
	}
	
	//�����ӿ��� ref�� ã�� ��ġ�� ��ȯ
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
	
	//victim�� ����� �������� ã�� data�� ��ü
	Integer Replace(Integer victim, Integer data)
	{
		
		int index = FindIndex(victim);
		frame[index][0] = data;
		return victim;
	}
	
	//�����ӿ� ������ �߰�
	void Add(int data)
	{
		if(cnt == capacity) 
		{
			System.out.println("�������� ������");
			return;
		}
		frame[cnt++][0] = data;
	}	
	//�������� ���¸� ���ڿ����·� ��ȯ
	boolean isFull() { return cnt >= capacity;}
	String FrameState() {

		String ret = "";
		for(int i=0; i<cnt; i++)
		{
			ret += frame[i][0] + " ";
		}
		return ret;
	}
	
	/*  Clock�� �ڿ�   */
	int framePointer; //������������
	
	//����������߻� �� ������Ʈ�� 0�� �������� ������� �̵��ϸ� ã��
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
	
	//������Hit�߻��� ������ ���������� ������������ �̵�
	void SetFramePointer(Integer ref)
	{
		int i = framePointer;
		while(frame[i][0] != ref)
		{
			frame[i][1] = 0;
			i = (i+1)%capacity;
		}
		framePointer = i;
		frame[i][1] = 1; //������Ʈ
	}
	
	//������������(�ε���) ��ȯ
	int GetFramePointer()
	{
		return framePointer;
	}
	Integer Replace(Integer data)
	{
		Integer victim = frame[framePointer][0]; //��������� ����
		frame[framePointer][0] = data; //�������� data�� ����
		frame[framePointer][1] = 1; //������Ʈ
		
		//�������� ��������찡 �ƴ϶��
		if(!isFull()) 
		{
			cnt++;
		}
		return victim; //��������ӹ�ȯ
	}

	
	/*  LRU�� �ڿ�  */
	
	//�ش� �ε����� �������� data�� ��ü�ϸ� ��ü�ð� ���
	Integer Replace(int index, Integer data, Integer time)
	{
		Integer victim = frame[index][0]; //��������� ����
		frame[index][0] = data;
		frame[index][1] = time; //�ð����
		return victim;
	}
	
	//�����ӿ� data�� �߰�
	void Add(Integer data, Integer time)
	{
		if(cnt == capacity) {
			System.out.println("�������� ������");
			return;
		}
		frame[cnt][0] = data;
		frame[cnt++][1] = time;
	}
	
	//�����ӿ� �ִ� data�� ã�� �����ð� ����
	void Update(Integer data, Integer time)
	{
		int index = FindIndex(data);
		frame[index][1] = time;
	}
	
	//���� ������ �������� ã�� ��ȯ
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


/* ��ǥ ������ ��ü ��å ���� */
class ReplacementPolicy
{
	
	//FIFO ��� : ���� ������ ������� ����� �������� ����
	static int FIFO(int[] refSequence, int capacity)
	{
		LinkedList<Integer> queue = new LinkedList<Integer>(); //ť ����
		FrameTable ft = new FrameTable(capacity); //�ѿ뷮�� capacity�� �����ӻ���
		int fcnt = 0; //���������� Ƚ��
		
		for(int i=0; i<refSequence.length; i++)
		{
			int f=0;
			if(ft.FindIndex(refSequence[i]) == -1) //������ ����
			{
				f = 1; 
				fcnt++; 
				queue.offer(refSequence[i]); //���� �������� ť�� ����
				
				if(ft.isFull())
				{
					int victim = queue.poll(); //��� �����Ӽ���
					ft.Replace(victim, refSequence[i]); //������ ��ü
				}
				else // �� �������� �ִٸ�
				{
					ft.Add(refSequence[i]);
				}
			}
		
			System.out.println("f:" + f +" ref:" + refSequence[i] + " frame: " +
					ft.FrameState());
		}
		return fcnt;
	}
	
	//LRU : �������� ���� ������ �������� ��� �����Ӽ���
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
				// �����ӿ��� refSequence[i] ã�� �ð��� ����
				ft.Update(refSequence[i], i);
			}
			System.out.println("f:" + f +" ref:" + refSequence[i] + " frame: " +
					ft.FrameState());
		}
		return fcnt;
	}
	
	
	//OPT : ������  ���� �ڿ� ���� �������� ������������� ����
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
			
			// ������Hit�� ��쿣 �۾� ����
			
			System.out.println("f:" + f +" ref:" + refSequence[i] + " frame: " +
			ft.FrameState());
		}
		
		return fcnt;
	}
	
	//CLOCK : �����������Ͱ� ����Ű�� �ִ� �������� ������������� ����
	static int CLOCK(int[] refSequence, int capacity)
	{
		FrameTable ft = new FrameTable(capacity);
		int fcnt = 0;
		for(int i=0; i<refSequence.length; i++)
		{
			int f=0;
			if(ft.FindIndex(refSequence[i]) == -1) // ����������
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
		
		System.out.println("<������ ���� ��>");
		System.out.println("FIFO:" + fifoCnt);
		System.out.println("LRU:" + lruCnt);
		System.out.println("OPT:" + optCnt);
		System.out.println("CLOCK:" + clockCnt);	}

}
