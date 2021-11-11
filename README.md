# BlueVisualizer

jdk version : 11

type, instance, vcd 파일이 미리 준비되어 있어야 합니다.

## test 방법.
1) 5stage pipeline (lab 6)

  - 아래 코드 실행.
  > cd .\out\artifacts\BlueVisualizer_jar
  
  > java -jar .\BlueVisualizer.jar
  
2) 3stage pipeline (lab 5)

  - .\out\artifacts\BlueVisualizer_jar\3stage 폴더의 소스 파일들(instance.txt, test.vcd, type.txt)로 상위 폴더 동일명의 소스파일을 덮어씌운 후 위의 코드 실행. 


## 실행 결과

5 stage는 f2d d2e e2m m2w 4개의 FIFO로 구성되어 있으며 시각화된 결과가 일관성이 있습니다.\n
3 stage는 시각화는 되지만 stall에 대한 정보가 부정확합니다. 기존에 stall을 구현할 때 5 stage FIFO의 empty, deque, enqueue status를 기준으로 해석하였는데 3 stage FIFO들의 경우 기존 FIFO들과는 다른 양상으로 나타나는 문제점이 있어서 해결방법을 모색중입니다.
