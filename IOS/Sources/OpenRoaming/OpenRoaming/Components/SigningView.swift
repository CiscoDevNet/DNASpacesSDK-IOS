//
//  SigningView.swift
//  OpenRoaming
//
//  Created by Luis Vasquez on 23/04/20.
//  Copyright © 2020 Cisco. All rights reserved.
//

import UIKit
import WebKit

public class SigningView: UIView, WKUIDelegate, WKNavigationDelegate {
    
    @IBOutlet internal var contentView: UIView!
    @IBOutlet internal weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet internal weak var webView: WKWebView!
    
    private var signingView:SigningView!
    private var sharedPrefs:SharedPrefs!
    private var signingHandler: SigningHandler!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        commonInit()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
        commonInit()
    }
    
    private func commonInit() {
        let bundle = Bundle(identifier:"com.cisco.OpenRoaming")
        bundle!.loadNibNamed("SigningView", owner: self, options: nil)
        addSubview(contentView)
        contentView.frame = self.bounds
        contentView.autoresizingMask = [.flexibleHeight, .flexibleWidth]
        
        setup()
    }
    
    private func setup() {
        activityIndicator.hidesWhenStopped = true
        webView.customUserAgent = "Mozilla/5.0 (iPhone; CPU iPhone OS 13_0_0 like Mac OS X) AppleWebKit/537.36 (KHTML, like Gecko) Version/6.0 Mobile/15E148 Safari/537.36"
        webView.uiDelegate = self
        webView.navigationDelegate = self
    }
    
    internal func load(urlString: String, signingView:SigningView, sharedPrefs:SharedPrefs, signingHandler: @escaping SigningHandler){
        
        HTTPCookieStorage.shared.removeCookies(since: Date.distantPast)
        print("[WebCacheCleaner] All cookies deleted")
        
        WKWebsiteDataStore.default().fetchDataRecords(ofTypes: WKWebsiteDataStore.allWebsiteDataTypes()) { records in
            records.forEach { record in
                WKWebsiteDataStore.default().removeData(ofTypes: record.dataTypes, for: [record], completionHandler: {})
                print("[WebCacheCleaner] Record \(record) deleted")
            }
        }
        
        self.signingView = signingView
        self.sharedPrefs = sharedPrefs
        self.signingHandler = signingHandler
        
        if let url = URL(string: urlString){
            var request = URLRequest(url: url)
            request.addValue(sharedPrefs.sdkToken!, forHTTPHeaderField: "Authorization")
            request.addValue("application/json", forHTTPHeaderField: "Content-Type")
            signingView.webView.load(request)
        }
        else {
            signingHandler(OpenRoamingError.LoginFailed)
        }
    }
    
    public func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        // show activity indicator
        activityIndicator.startAnimating()
    }

    public func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        // hide activity indicator
        activityIndicator.stopAnimating()
    }
    public func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        // hide activity indicator
        activityIndicator.stopAnimating()
    }
           
    public func webView(_ webView: WKWebView, decidePolicyFor navigationAction: WKNavigationAction, decisionHandler: @escaping (WKNavigationActionPolicy) -> Void) {
        
        if let absotuleUrl = navigationAction.request.url?.absoluteString {
            print ("URL :",absotuleUrl)
            if absotuleUrl.contains("api/profiles/success") {
                signingHandler(nil)
            }
        }
        decisionHandler(.allow)
    }
}
