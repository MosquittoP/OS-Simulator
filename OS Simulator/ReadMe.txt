OS Simulator

사용 언어 : Java

OS의 자원관리 과정을 확인하는 시뮬레이터입니다.
각 작업의 우선순위를 정하는 CPU Scheduler와 가상 메모리 사용 시 메인 메모리에 페이지를 적재하는 교체 알고리즘인 Page Management 프로그램 총 2개 입니다.

CPU Scheduler에는 FIFO, SJF, Priority, HRN, RR(Round Robin)방법이 있으며, SJF, Priority에는 선점, 비선점 구분하여 확인 가능합니다.
출력 시, 모든 단계의 과정과 간트차트, 포인터의 위치, 그리고 각 방법에 대한 평균대기시간과 평균반환시간이 출력됩니다.

Page Management에는 FIFO, LRU, OPT, Clock방법이 있으며, 각 방법을 이용했을 때 페이지 교체 과정과 페이지 부재 발생 횟수를 출력합니다.