//
//  CameraView.swift
//  Golf
//
//  Created by Alexander Skorokhodov on 26.08.2023.
//

import SwiftUI
import AVFoundation

struct CameraView: View {
    
    @EnvironmentObject var cameraModel: CameraViewModel
    
    var body: some View {
        
        GeometryReader{ proxy in
            let size = proxy.size
            
            CameraPreview(cameraModel: cameraModel, size: size).environmentObject(cameraModel)
        }
        .onAppear(perform: {
            
            cameraModel.checkPermission()
        })
        .alert(isPresented: $cameraModel.alert) {
            Alert(title: Text("Please enable access to camera and microphone"))
        }
    }
    
}


struct CameraPreview: UIViewRepresentable {
    
    func updateUIView(_ uiView: UIView, context: Context) {
        
    }
    
    @ObservedObject var cameraModel: CameraViewModel
    var size: CGSize
    
    func makeUIView(context: Context) ->  UIView {
        let view = UIView()
        
        cameraModel.preview = AVCaptureVideoPreviewLayer(session: cameraModel.session)
        cameraModel.preview.frame.size = size
        
        cameraModel.preview.videoGravity = .resizeAspectFill
        view.layer.addSublayer(cameraModel.preview)
        
        cameraModel.session.startRunning()
        
        return view
    }
}

#Preview {
    CameraView()
}
