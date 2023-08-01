/**
 * 화상 채팅에 필요한 js
 */

document.addEventListener('DOMContentLoaded', () => {

	// 신고 여부
	let reportForm = document.querySelector('form#reportForm');
	let report = false;
	let btnReport = document.querySelector('button#btnReport');

	// 화면 공유를 해제하는 버튼
	const btnScreen = document.querySelector('button#btnScreen');
	// 화면이 송출 중인지 여부에 대해 표시(false=쉬는중 / true=송출중)
	let isScreenSharing = true;

	// 음소거/해제 를 설정하는 버튼
	const btnMute = document.querySelector('button#btnMute');
	let isMute = false;
	// 오퍼 연결을 전달하는 버튼
	let btnOffer = document.querySelector('button#btnOffer');

	// 방번호
	let roomId = document.querySelector('input#roomId').value;
	btnOffer.addEventListener('click', createOffer);

	// MyFace = 내 카메라, peerFace = 상대 카메라, myStream = 내 카메라에서 나오는 영상 스트림
	let myFace = document.getElementById("myFace");
	let peerFace = document.querySelector('video#peerFace');
	let myStream;

	// wss 주소로 웹소켓을 연결(채팅에는 ws로도 가능하나, RTC는 반드시 Security 연결 필요)
	let wss = 'wss://' + location.host + `/facechat?roomid=${roomId}`;


	let conn = new WebSocket(wss);

	// RTC 객체 생성 후 해당 객체에 Stun 서버 주소 전달.
	let configuration = {
		"iceServers": [{
			"url": "stun:stun.l.google.com:19302"
		}]
	}
	let myPeerConnection = new RTCPeerConnection(configuration);

	// mediaDevices 를 통해 영상, 음성 기기들을 찾고, 거기서 기기를 가져옴.
	async function getMedia() {
		try {
			myStream = await navigator.mediaDevices.getUserMedia({
				audio: true,
				video: true
			});

			// 불러온 카메라를 HTML에 연결함. 이때부터 카메라에 담기는 화면이 웹에 표시됨.
			myFace.srcObject = myStream;
		} catch (e) {
			console.log("Media Link Error");
		}
	}

	// 오퍼 생성 시 해당 함수가 호출됨.
	async function createOffer() {
		console.log("Offer Send");

		// 아까 작성한 카메라 연결 함수 호출.
		await getMedia();

		// RTC Connection에 내 화면에서 송출되는 영상, 소리를 담음.
		await myStream.getTracks().forEach((track) => myPeerConnection.addTrack(track, myStream));

		// RTC 객체 생성 -> RTC 커넥션에 트랙(영상,소리) 담기 -> Offer 전달(SDP 메시지 전달)
		let offer = await myPeerConnection.createOffer();
		console.log("오퍼 전달")
		// 이제 send 로 오퍼 전달.

		// TODO 여기 await 지움.
		send({
			event: "offer",
			data: offer
		})
		console.log("Offer send Success")
		await myPeerConnection.setLocalDescription(offer);
	}




	// 상대가 나를 연결할 수 있는 방법들을 만들어 전달.
	myPeerConnection.onicecandidate = function(event) {
		console.log("candidate send");
		send({
			event: "candidate",
			data: event.candidate
		})
	}
	// 연결이 되서 피어의 스트림이 내 RTC객체에 등록되면 자동 호출.
	myPeerConnection.addEventListener("addstream", handleAddStream);
	function handleAddStream(data) {
		console.log("Streaming Data Receive");

		// 연결된 피어의 스트림을 웹의 상대화면 부분에 표시
		peerFace.srcObject = data.stream;

		// 녹음 시작
		audiorecording();


		// 성공적으로 Facechatroom이 만들어졌다고 판단되어 방을 만드는 api 호출.

	}

	// 소켓 연결 시 실행할 함수
	conn.onopen = function() {
		console.log("웹소켓 연결 완료.");
	};

	// 소켓에서 메시지 수신 시 실행할 함수
	conn.onmessage = async function(msg) {
		let content = JSON.parse(msg.data);
		if (content.event == "offer") {
			console.log("Offer Receive");
			// Offer가 오면 Offer를 RemoteDescription에 등록함.
			let offer = content.data;
			await myPeerConnection.setRemoteDescription(offer);
			// 수신한 쪽에도 내 카메라에서 송출되면 화면 연결.
			await getMedia();
			await myStream.getTracks().forEach((track) => myPeerConnection.addTrack(track, myStream));
			// answer 생성 후 LocalDescription에 전달.
			let answer = await myPeerConnection.createAnswer();
			await myPeerConnection.setLocalDescription(answer);
			console.log("Answer Send");
			send({
				event: "answer",
				data: answer
			})
		} else if (content.event == "answer") {

			// answer를 수신한 경우, remoteDescription에 전달.
			console.log("Answer Receive");
			answer = content.data;
			await myPeerConnection.setRemoteDescription(answer);
		} else if (content.event == "candidate") {
			console.log("candidate Receive");
			// RemoteDescription에 연결된 상대와 나의 연결방식을 결정.
			await myPeerConnection.addIceCandidate(content.data);
		}
	}

	// 웹소켓 서버로 메시지 전달하는 함수
	function send(message) {
		conn.send(JSON.stringify(message));
	}
	
	
	
	// 7월 31일 밤 추가 TODO
	const mutechange = () => {
		navigator.mediaDevices.getUserMedia({ audio: true })
			.then(stream => {
				// 오디오 트랙 가져오기

				const audioTracks = stream.getAudioTracks()[0];
				if (isMute == false) {
					// 음소거 처리
					console.log("mute");
					audioTracks.forEach((track) => {
						track.enabled = false;
					}); // true로 설정하면 다시 음소거가 해제됩니다.
					btnMute.innerHTML = '마이크 활성화';
				} else {
					console.log("non-mute");
					audioTracks.forEach((track) => {
						track.enabled = true;
					});
					btnMute.innerHTML = '마이크 음소거';
				}
				isMute = !isMute;
			})
			.catch(error => {
				console.error('Error accessing local media:', error);
			});
	}


	// 7월 31일 밤 추가 TODO
	btnMute.addEventListener('click', mutechange);





	// 화면 공유 중지 버튼 클릭 이벤트 처리 ( 7월 31일 밤 추가 TODO )
	btnScreen.addEventListener('click', async () => {

		// 스트리밍 자체가 시작되기 전인 경우
		if (!myStream) return;

		// 스트리밍 되는 게 없음
		if (myStream.getVideoTracks().length === 0) return;

		if (!isScreenSharing) {
			console.log("Video show");
			btnScreen.innerHTML = '화면 숨김';
			myStream.getVideoTracks()[0].enabled = true; // 원래 화면 송출 상태로 변경
			myFace.srcObject = myStream;
		} else {
			console.log("Video hide");
			btnScreen.innerHTML = '화면 표시';
			myStream.getVideoTracks()[0].enabled = false; // 화면 송출 중지
			myFace.srcObject = null;
		}

		isScreenSharing = !isScreenSharing;








	});


	let audioContext;
	let mediaStreamSource;
	let recorder;
	let audioChunks = [];
	let recordedBlob;

	async function audiorecording() {

		try {
			const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
			audioContext = new (window.AudioContext || window.webkitAudioContext)();
			mediaStreamSource = audioContext.createMediaStreamSource(stream);

			// 스트림을 녹음할 노드 생성
			recorder = new MediaRecorder(stream);
			console.log(recorder);

			recorder.ondataavailable = (event) => {
				if (event.data.size > 0) {
					audioChunks.push(event.data);
				}
			};

			recorder.onstop = () => {
				// 녹음이 완료된 후에 녹음된 음성 데이터를 처리하거나 서버에 업로드할 수 있습니다.
				recordedBlob = new Blob(audioChunks, { type: 'audio/wav' });
				// 여기에서 녹음된 음성 데이터를 처리하거나 서버에 업로드할 수 있습니다.

			};

			recorder.start();
		} catch (err) {
			console.error('마이크 접근 권한을 얻지 못했습니다.', err);
		}
	}

	btnReport.addEventListener('click', async () => {

		let answerRes = confirm('정말로 신고하시겠습니까? 신고하시면 곧바로 통화가 종료됩니다.');

		if (!answerRes) {

			return;

		}

		// recordedBlob 레코드 파일.
		// 녹음 중단
		console.log(recorder);
		recorder.stop();



		const formData = new FormData();
		formData.append('audioFile', recordedBlob);

		await fetch(`/facechat/report?roomId=${roomId}`, {
			method: 'POST',
			body: formData,
		})
			.then(response => response.text())
			.then(result => console.log(result))
			.catch(error => console.error('파일 업로드 실패:', error));



	})


})

