//
//  Home.swift
//  Golf
//
//  Created by Alexander Skorokhodov on 25.08.2023.
//

import SwiftUI
import AVKit
import UIKit

extension UIScreen{
   static let screenWidth = UIScreen.main.bounds.size.width
   static let screenHeight = UIScreen.main.bounds.size.height
   static let screenSize = UIScreen.main.bounds.size
}

extension Dictionary where Value: Equatable {
    func findKey(forValue val: Value) -> Key? {
        return first(where: { $1 == val })?.key
    }
}


struct Home: View {
    
    @StateObject var cameraModel = CameraViewModel()
    @State var isLoading: Bool = false
    @State var comment: String = "loading.."
    @State var view: String = "loading.."
    @State var error: String = ""
    @State var frames: [Image] = []

    
    var body: some View {
        VStack() {
            // Camera view
            CameraView().environmentObject(cameraModel)
            
            // Controls
            HStack() {
                
                Button(action: {}){label: do {
                    Label
                    {
                        Image(systemName: "chevron.right")
                            .font(.callout)
                    } icon:
                    {
                        Text("Preview")
                            .opacity(0)
                    }
                    .opacity(0)
                    .padding(.horizontal, 20)
                    .padding(.vertical, 8)
                    .background{
                    }}}
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
                Button(action: {
                    if (cameraModel.isRecordingExists) {
                        isLoading = true
                        uploadMedia(videoURL: cameraModel.previewURL!)
                        print(comment, view, error)
                        cameraModel.showPreview.toggle()
                        isLoading = false
                    }
                }) { label: do {
                    Label {
                        Image(systemName: "chevron.right")
                            .font(.callout)
                            .opacity(cameraModel.isRecordingExists ? 1 : 0)
                    } icon: {
                        Text("Preview")
                            .opacity(cameraModel.isRecordingExists ? 1 : 0)
                    }
                    .padding(.horizontal, 20)
                    .padding(.vertical, 8)
                    .background{
                        Capsule().fill(.white)
                            .opacity(cameraModel.isRecordingExists ? 1 : 0)
                    }
                }
                }
                
                
            }
        }
        .overlay(
            content: {
                if isLoading {
                    LoadingView()
                } else if let url = cameraModel.previewURL, cameraModel.showPreview {
                    FinalPreview(url: url, comment: comment, error: error, view: view, showPreview: $cameraModel.showPreview, frames: frames)
                        .transition(.move(edge: .trailing))
                }
            }
        )
        .animation(.easeInOut, value: cameraModel.showPreview)
    }
    

// upload event
func uploadMedia(videoURL: URL) {
    guard let requestURL = URL(string: "https://9190-77-234-219-9.ngrok-free.app/get_review") else {
        print("Некорректный URL для отправки видео")
        return
    }
    
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
            
            let jsonString = String(data: data!, encoding: .utf8)
            let res_dict = convertToDictionary(text: jsonString!)
            
            
            self.comment = res_dict?["comment"]! as! String
            self.view = res_dict?["racurs"]! as! String
            self.error = res_dict?["errors"]! as! String
            self.frames = [Image(uiImage: UIImage(data: Data(base64Encoded: res_dict?["1"]! as! String)!)!),Image(uiImage: UIImage(data: Data(base64Encoded: res_dict?["2"]! as! String)!)!),Image(uiImage: UIImage(data: Data(base64Encoded: res_dict?["3"]! as! String)!)!),Image(uiImage: UIImage(data: Data(base64Encoded: res_dict?["4"]! as! String)!)!),Image(uiImage: UIImage(data: Data(base64Encoded: res_dict?["5"]! as! String)!)!),Image(uiImage: UIImage(data: Data(base64Encoded: res_dict?["7"]! as! String)!)!),Image(uiImage: UIImage(data: Data(base64Encoded: res_dict?["8"]! as! String)!)!),Image(uiImage: UIImage(data: Data(base64Encoded: res_dict?["10"]! as! String)!)!)]
//            if self.error != "" {
//                let sh = saveImage(imageToSave: UIImage(data: Data(base64Encoded: res_dict?["Head movement"]! as! String)!)!, filename: "Head movement.jpg")
//                //            self.image_head = UIImage(data: res_dict?["Head movement"]! as! Data)!
//            }
            
            
        }.resume()
    } catch {
    }
    
} }

func convertToDictionary(text: String) -> [String: Any]? {
    if let data = text.data(using: .utf8) {
        do {
            return try JSONSerialization.jsonObject(with: data, options: []) as? [String: Any]
        } catch {
            print(error.localizedDescription)
        }
    }
    return nil
}


struct FinalPreview: View {
    @State var isOpenFrames: Bool = false
    var url: URL
    var comment: String = "fdjhkdfsjahfdksajh"
    var error: String = "239fhcmdk"
    var view: String = "wqopworirq"
    @Binding var showPreview: Bool
    var frames: [Image]
    
    var body: some View {
        GeometryReader{proxy in
            let size = proxy.size
            HStack{
                VStack{
                HStack {
                    Button {
                        showPreview.toggle()
                    } label: {
                        Label {
                            Text("Back")
                        } icon: {
                            Image(systemName: "chevron.left")
                        }
                        .foregroundColor(nil)
                    }
                    .padding(.leading)
                    .padding(.top, 22)
                    Spacer()
                }
                Divider()
                    if !isOpenFrames{
                        VideoPlayer(player: AVPlayer(url: url))
                            .aspectRatio(contentMode: /*@START_MENU_TOKEN@*/.fill/*@END_MENU_TOKEN@*/)
                            .frame(width: size.width, height: size.height / 2)
                            .overlay(alignment: .topLeading)
                        { }
                        Spacer()}
                
                HStack {
                    Text("Comment")
                    Spacer()
                } 
                .padding(.leading)
                HStack {
                    Text(comment)
                    Spacer()
                } 
                .padding(.leading)
                Spacer()
                HStack {
                    Text("View")
                    Spacer()
                } 
                .padding(.leading)
                HStack {
                    Text(view)
                    Spacer()
                }
                .padding(.leading)
                    
                Spacer()
                HStack {
                    Text(error != "" ? "Error" : "")
                    Spacer()
                }
                .padding(.leading)
                HStack {
                    Text(error)
                    Spacer()
                }
                .padding(.leading)
                Button(){
                    isOpenFrames.toggle()
                } label: {
                    Label {
                        if !isOpenFrames {
                            Text("Open frames")
                        } else {
                            Text("Close frames")
                        }
                        
                    } icon: {
                        Image(systemName: "photo.on.rectangle")
                    }
                    .foregroundColor(nil)
                }
                    if isOpenFrames{StoreView(_frames: frames);Spacer()}
                    
            }
            .frame(width: size.width, height: size.height)
            .background()
        }
    }
    }
}

struct LoadingView: View {
    var body: some View{
        Text("Подождите..")
    }
}

func saveImage(imageToSave: UIImage, filename: String) -> URL {
    if let documentsDirectory = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first {
        // Создайте имя файла (может быть уникальным, чтобы избежать перезаписи)
        let fileURL = documentsDirectory.appendingPathComponent(filename)
        
        // Преобразуйте UIImage в Data
        if let imageData = imageToSave.jpegData(compressionQuality: 1.0) {
            // Сохраните данные в файл
            do {
                try imageData.write(to: fileURL)
                print("Изображение успешно сохранено: \(fileURL)")
                return fileURL
            } catch {
                print("Ошибка при сохранении изображения: \(error)")
                
            }
        }
    }
    return URL(string: "")!
}

#Preview {
    Home()
}
