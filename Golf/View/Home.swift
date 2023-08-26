//
//  Home.swift
//  Golf
//
//  Created by Alexander Skorokhodov on 25.08.2023.
//

import SwiftUI
import AVKit

extension UIScreen{
   static let screenWidth = UIScreen.main.bounds.size.width
   static let screenHeight = UIScreen.main.bounds.size.height
   static let screenSize = UIScreen.main.bounds.size
}

struct Home: View {
    
    @StateObject var cameraModel = CameraViewModel()
    @State var isLoading: Bool = false
    
    
    var body: some View {
        VStack() {
            // Camera view
            CameraView().environmentObject(cameraModel)
            
            // Controls
            HStack() {
                
                Button {
                } label: {
                    Label {
                        
                        Image(systemName: "chevron.right")
                            .font(.callout)
                        
                    } icon:
                    {
                        Text("Preview")
                    }.padding(.horizontal, 20)
                        .padding(.vertical, 8)
                        .background{
                            Capsule().fill(.black)
                        }
                    
                }.frame(maxHeight: 50, alignment: .bottom)
                    .padding(.bottom, 10)
                    .padding(.bottom, 30)
                Spacer()
                Button {
                    if cameraModel.isRecording {
                        cameraModel.stopRecording()
                    } else {
                        cameraModel.startRecording()
                    }
                } label: {
                    
                    Image(systemName: "figure.golf")
                        .resizable()
                        .renderingMode(/*@START_MENU_TOKEN@*/.template/*@END_MENU_TOKEN@*/)
                        .padding(5)
                        .aspectRatio(contentMode: .fit)
                        .cornerRadius(35)
                        .opacity(cameraModel.isRecording ? 0 : 1)
                        .frame(width: 35, height: 70)
                        .foregroundColor(.white)
                        .background {
                            Circle()
                                .fill(cameraModel.isRecording ? .green : .black)
                                .stroke(cameraModel.isRecording ? .green : .white, lineWidth: 3)
                                .frame(width: 70, height: 70)
                            
                        }
                    
                    
                }
                Spacer()
//                Button(){
//                    cameraModel.showPreview.toggle()
//                } label: {
//                    Label
//                    {
//                        Image(systemName: "chevron.right")
//                            .font(.callout)
//                    } icon:
//                    {
//                        Text("Preview")
//                    }
//                    .padding(.horizontal, 20)
//                    .padding(.vertical, 8)
//                    .background{
//                        Capsule().fill(.white)
//                    }
//                }
//                .onTapGesture {
//                    Task {
//                        isLoading = true
//                        let _url = cameraModel.previewURL
//                        print(_url, _url?.path())
//                        await uploadMedia(videoURL: _url!)
//                        isLoading = false
//                    }
//                }
                Button(action: { uploadMedia(videoURL: cameraModel.previewURL! );cameraModel.showPreview.toggle()}){Text("Analyse").background(.blue)}
                .frame(maxHeight: 70, alignment: .bottom)
                .padding(.bottom, 10)
                .padding(.bottom, 30)
            }
            .foregroundColor(.black ).padding(2)
            
        }
        .overlay(
            content: {
                if let url = cameraModel.previewURL, cameraModel.showPreview {
                    FinalPreview(url: url, showPreview: $cameraModel.showPreview)
                        .transition(.move(edge: .trailing))
                }
            }
        )
        .overlay(
            content: {
                if isLoading == true {
                    LoadingView()
                }
            }
        )
        .animation(.easeInOut, value: cameraModel.showPreview)
    }}
    

// upload event
func uploadMedia(videoURL: URL) {
    guard let requestURL = URL(string: "https://9190-77-234-219-9.ngrok-free.app/get_review") else {
        print("Некорректный URL для отправки видео")
        return
    }
    
    // Создаем URLRequest с переданным URL
//    var request = URLRequest(url: requestURL)
    //        request.httpMethod = "POST"
    //
    //        // Устанавливаем заголовок Content-Type для определения типа данных как видео
    //        request.setValue("video/mp4", forHTTPHeaderField: "Content-Type")
    //        print(type(of: videoURL))
    //        // Создаем объект URLSessionUploadTask для отправки файла
    //        let task = URLSession.shared.uploadTask(with: request, fromFile: videoURL) { (data, response, error) in
    //            if let error = error {
    //                print("Ошибка при отправке видео: \(error.localizedDescription)")
    //                return
    //            }
    //
    //            // Обрабатываем ответ от сервера
    //            if let httpResponse = response as? HTTPURLResponse {
    //                if httpResponse.statusCode == 200 {
    //                    print("Видео успешно отправлено на сервер")
    //                } else {
    //                    print("Ошибка сервера. Код ответа: \(httpResponse.statusCode)")
    //                }
    //            }
    //        }
    //
    //        // Запускаем задание для отправки видео
    //        task.resume()
    
        
//        do {
//            let data = try Data(contentsOf: videoURL)
//            let base53 = data.base64EncodedString()
//            let net = Networking()
//            net.sendPostRequest(to: requestURL, body: data)
//        } catch {
//            
//        }
    do {
        let data = try Data(contentsOf: videoURL)
        let base53 = data.base64EncodedString()
        
        let body: [String: String] = ["video": base53]
        let finalBody = try JSONSerialization.data(withJSONObject: body)
        
        var request = URLRequest(url: requestURL)
        request.httpMethod = "POST"
        request.httpBody = finalBody
        
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        
        URLSession.shared.dataTask(with: request) { (data, response, error) in
            print(data)
        }.resume()
    } catch {}
    
    }



struct FinalPreview: View {
    var url: URL
    @Binding var showPreview: Bool
    
    var body: some View {
        GeometryReader{proxy in
            let size = proxy.size
            VideoPlayer(player: AVPlayer(url: url))
                .aspectRatio(contentMode: /*@START_MENU_TOKEN@*/.fill/*@END_MENU_TOKEN@*/)
                .frame(width: size.width, height: size.height)
                .overlay(alignment: .topLeading) {
                    Button {
//                        Py().helloworld()
                        showPreview.toggle()
                    } label: {
                        Label {
                            Text("Back")
                        } icon: {
                            Image(systemName: "chevron.left")
                        }
                        .foregroundColor(.white)
                    }
                    .padding(.leading)
                    .padding(.top, 22)
                }
        }
    }
}

struct LoadingView: View {
    var body: some View{
        Text("Подождите..")
    }
}
struct Networking {
    var urlSession = URLSession.shared

    func sendPostRequest(
        to url: URL,
        body: Data
//        then handler: @escaping (Result<Data, Error>) -> Void
    ) {
        // To ensure that our request is always sent, we tell
        // the system to ignore all local cache data:
        var request = URLRequest(
            url: url,
            cachePolicy: .reloadIgnoringLocalCacheData
        )
        
        request.httpMethod = "POST"
        request.httpBody = body

        let task = urlSession.dataTask(
            with: request
        )

        task.resume()
    }
}
#Preview {
    Home()
}
