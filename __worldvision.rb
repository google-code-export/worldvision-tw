require 'rubygems'
# require 'sinatra_more'
require 'sinatra'
require 'dm-core'
require 'appengine-apis/users'
require 'appengine-apis/urlfetch'
require "net/http"
require "uri"
require "java"

include_class 'com.google.appengine.api.urlfetch.HTTPHeader'
include_class 'com.google.appengine.api.urlfetch.HTTPResponse'
include_class 'com.google.appengine.api.urlfetch.URLFetchService'
include_class 'com.google.appengine.api.urlfetch.URLFetchServiceFactory'
include_class 'java.net.URL'

# model
class Account
  include DataMapper::Resource
  
  property :id, Serial
  property :account, String
  property :password, String
  property :role, String
  property :name, String
  property :email, String
  property :voulenteer_id, String
  property :voulenteer_type, String
  property :jobs, Integer, :default => 0
end
class Country
  include DataMapper::Resource
  property :id, Serial
  property :name, String
  property :note_url, String
  property :noun_url, String
  property :template_url, String
  property :note_file_name, String
  property :noun_file_name, String
  property :template_file_name, String
end

class VoulenteerLog
  include DataMapper::Resource
  
  property :id, Serial
  property :voulenteer_id, String
  property :voulenteer_name, String
  property :return_date, Date
  property :excuse, String
  property :claim_date, Date
  property :letter_id, String 
end

class Letter
  include DataMapper::Resource
  property :id, Serial
  property :create_date, Date
  property :employee_id, String
  property :country_name, String
  property :country_id, String
  property :type, String
  property :trans_type, String
  property :note, String
  property :status, String
  property :number_of_letters, Integer, :default  => 0
  property :voulenteer_id, String
  property :voulenteer_name, String
  property :claim_date, Date
  property :due_date, Date
  property :due_date_3, Date
  property :return_date, Date
  property :upload_file, Text
  property :upload_file_url, String
  property :upload_file_name, String
  property :return_file, String
  property :return_file_url, String
  property :return_file_name, String
  property :show, String
  property :send_due_reminder, Boolean, :default => 0
  
  def self.upload_file(upload)
      Letter.create(:upload_file=>upload)
  end
  def upload_file
      Letter.create(:upload_file=>@file)
  end
end

class Date
  def to_s
    strftime('%m/%d/%Y')
  end
end

# end

# Configure DataMapper to use the App Engine datastore 
DataMapper.setup(:default, "appengine://auto")

# Make sure our template can use <%=h
helpers do
  include Rack::Utils  
  alias_method :h, :escape_html
  
  def protected!
      unless admin?
        response['WWW-Authenticate'] = %(Basic realm="Login to World Vision")
        throw(:halt, [401, "Not authorized\n"])
      end
  end
  
  def employee!
    unless employee?
      response['WWW-Authenticate'] = %(Basic realm="Login to World Vision")
      throw(:halt, [401, "Not authorized\n"])
    end
  end
  
  def voulenteer!
    unless voulenteer?
      response['WWW-Authenticate'] = %(Basic realm="Login to World Vision")
      throw(:halt, [401, "Not authorized\n"])
    end
  end

  def admin?
      @auth ||=  Rack::Auth::Basic::Request.new(request.env)
      @auth.provided? && @auth.basic? && @auth.credentials && @auth.credentials == ['admin', 'admin']
  end
  
  def employee?
      @auth ||=  Rack::Auth::Basic::Request.new(request.env)
      @auth.provided? && @auth.basic? && @auth.credentials && authenticate_account(@auth, 'employee')  
  end
  
  def voulenteer?
      @auth ||=  Rack::Auth::Basic::Request.new(request.env)
      @auth.provided? && @auth.basic? && @auth.credentials && authenticate_account(@auth, 'voulenteer')
  end
  
  def current_user
    session[:user]
  end
  
  def authenticate_account(auth, type)
    # puts "debug:authen:" + auth.credentials.to_s
    id = auth.username
    account = nil
    accounts = Account.all
    # puts "debug:id" + id
    accounts.each do |_account|
      # puts "account" + _account.account
      if _account.account == id
        # "debug: find_account: " + _account.account
        account = _account 
      end
    end
    
    # account = Account.first(:name => 'robbie')
    if !account.nil?
      # puts "debug:authen:account:3: " + account.account + ":pwd: " + account.password + ": type: " + account.role
    end
    
    if !account.nil? && auth.credentials == [account.account, account.password] && account.role == type
       if current_user.nil?
         session[:user] = account
       end
       return true
    else
       return false
    end
  end
end


# get %r{/([\w]*)} do 
#   url = request.fullpath
#   # url = request.url
#   #     l_index = url.index('t/')
#   print "url: " + url[1,url.length]
#   # if l_index != nil
#   #       @target_url = url[l_index+2, url.length]
#   #       print "url2: " + @target_url
#   #   end
#   if url != nil
#     @target_url = url[1,url.length]
#   end
#   if @target_url && @target_url.index('http') == nil
#     @target_url = 'http://www.fliptop.com'
#   end  
#   erb :dodolo
# end

$blobstoreService = com.google.appengine.api.blobstore.BlobstoreServiceFactory.getBlobstoreService();

def get_upload_url()
  $blobstoreService.createUploadUrl("/upload")
end

def get_letters()
  # search
  trans_type = params[:type]
  trans_type = params[:type].nil?? nil : params[:type] == ''? nil : params[:type]
  country_id = params[:country_id].nil?? nil : params[:country_id] == ''? nil : params[:country_id].to_i
  employee_id = params[:employee_id].nil?? nil : params[:employee_id]==''? nil : params[:employee_id]
  date = params[:date].nil?? nil : params[:date]==''? nil : params[:date]
  
  
  
  # sorting
  sort = params[:sort]
  field = params[:field]
    

    
    @criteria = ''
    @letters = Letter.all()
    if (trans_type)
      @letters = @letters.all(:trans_type => trans_type)
      @criteria+=('type=' + trans_type)
    end
    if (country_id)
      @letters = @letters.all(:country_id=>country_id)
      @criteria+=('country_id='+country_id.to_s)
    end
    if (employee_id)
      @letters = @letters.all(:employee_id=>employee_id)
      @criteria+=('employee_id'+employee_id.to_s)
    end
    if (date)
              d = Date.strptime(date, DATE_FORMAT)
              letters = Array.new()
              index = 0
              puts ("date: " + date)
              puts ("d: " + d.to_s)
              puts "size" + @letters.size.to_s
              @letters = @letters.all(:create_date=>d)
              # @letters.each do |letter|
              #                 if (letter.create_date.to_s != d)
              #                   letters.push(letter)
              #                   puts "date: " + letter.create_date.to_s
              #                   puts "index" + index.to_s
              #                 end
              #                 index += 1
              #               end
              #               @letters.delete(letters)
              @criteria += ('date='+date.to_s)
    end
    if (sort)
      if (field)
        if (sort == 'asc')
          @letters = @letters.all(:order=>[:employee_id.asc])
          elsif(sort == 'desc')
          @letters = @letters.all(:order=>[:employee_id.desc])
        end
        @criteria += ('sort='+sort+'&filed='+field)
      else
        if (sort == 'asc')
          @letters = @letters.all(:order=>[:create_date.asc])
        elsif(sort == 'desc')
          @letters = @letters.all(:order=>[:create_date.desc])
        end
        @criteria += ('sort='+sort)
      end
    else
      @letters = @letters.all(:order=>[:create_date.asc])
    end
    @letters
end

# end

