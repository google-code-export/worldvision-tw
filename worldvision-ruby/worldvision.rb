require 'rubygems'
# require 'sinatra_more'
require 'sinatra'
require 'dm-core'
require 'appengine-apis/users'
require 'appengine-apis/urlfetch'
require "net/http"
require "uri"
require "java"
use Rack::Logger

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
  property :weekly_email, Boolean, :default  => true
  property :allow_login, Boolean, :default  => true
  property :note, String
end
class Country
  include DataMapper::Resource
  property :id, Serial
  property :name, String
  property :continent, String
  property :note_url, String
  property :noun_url, String
  property :template_url, String
  property :background_url, String
  property :note_file_name, String
  property :noun_file_name, String
  property :template_file_name, String
  property :background_file_name, String

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
  property :letter_source_type, String
  property :trans_type, String
  property :note, String
  property :status, String
  property :number_of_letters, Integer, :default  => 0
  property :voulenteer_id, String
  property :voulenteer_name, String
  property :voulenteer_account, String
  property :claim_date, Date
  property :due_date, Date
  property :due_date_3, Date
  property :return_date, Date
  property :return_days, Integer
  property :upload_file, Text
  property :upload_file_url, String
  property :upload_file_name, String
  property :return_file, String
  property :return_file_url, String
  property :return_file_name, String
  property :show, String
  property :send_due_reminder, Boolean, :default => 0
  property :deleted, Boolean, :default => 0
  property :re_upload, Boolean, :default => 0

  def self.upload_file(upload)
    Letter.create(:upload_file=>upload)
  end

  def upload_file
    Letter.create(:upload_file=>@file)
  end
end

class Template
  include DataMapper::Resource
  property :id, Serial
  property :tempate_url, String
end

class News
  include DataMapper::Resource
  property :id, Serial
  property :created_date, Date
  property :title, String
  property :content, String
  property :status, String
end

class Date
  def to_s
    strftime('%m/%d/%Y')
  end
end


# end

# Configure DataMapper to use the App Engine datastore 
DataMapper.setup(:default, "appengine://auto")
enable :sessions
# Make sure our template can use <%=h
helpers do
  include Rack::Utils
  alias_method :h, :escape_html

  def protected!
    unless admin?
      redirect '/login'
#      response['WWW-Authenticate'] = %(Basic realm="Login to World Vision")
#      throw(:halt, [401, "Not authorized\n"])
    end
  end

  def employee!
    unless employee?
      redirect '/login'
#      response['WWW-Authenticate'] = %(Basic realm="Login to World Vision")
#      throw(:halt, [401, "Not authorized\n"])
    end
  end

  def voulenteer!
    unless voulenteer?
      redirect '/login'
#      response['WWW-Authenticate'] = %(Basic realm="Login to World Vision")
#      throw(:halt, [401, "Not authorized\n"])
    end
  end

  def admin?
    allowed('admin')
#    @auth ||=  Rack::Auth::Basic::Request.new(request.env)
#    @auth.provided? && @auth.basic? && @auth.credentials && @auth.credentials == ['admin', 'admin']
  end

  def employee?
    allowed('employee')
#    @auth ||=  Rack::Auth::Basic::Request.new(request.env)
#    @auth.provided? && @auth.basic? && @auth.credentials && authenticate_account(@auth.username, @auth.credentials, 'employee')
  end

  def voulenteer?
    allowed('voulenteer')
#    @auth ||=  Rack::Auth::Basic::Request.new(request.env)
#    @auth.provided? && @auth.basic? && @auth.credentials && authenticate_account(@auth.username, @auth.credentials, 'voulenteer')
  end

  def allowed(role)
    account = current_user
    if (account && account.role == role)
      true
    else
      false
    end
  end

  def current_user
    if session[:user]
      if (session[:user] == 'admin')
        account = Account.new
        account.role = 'admin'
        account
      else
        Account.first(:account => session[:user])
      end
    else
      nil
    end
  end

  def logger
    request.logger
  end

  def truncate (string)
    if (string)
      index = string.rindex('\\')
      if (index && index > 0)
        string = string[index+1, string.length]
      end
    end
    string
  end

  def is_chinese_email(type)
    if (type && type == 'chi')
      true
    else
      false
    end
  end

  def is_english_email(type)
    if (type && type == 'eng')
      true
    else
      false
    end
  end

  def letter_status_to_chinese(status)
    if (status == "emergent")
      "緊急"
    elsif (status == "unclaimed")
      "未領取"
    elsif (status == "claimed")
      "已領取"
    elsif (status == "returned")
      "已譯返"
    end
  end

  def authenticate_account(username, password, type)

    if (username == 'admin' && password == 'admin')
      session[:user] = 'admin'
      return 'admin'
    end

    # puts "debug:authen:" + auth.credentials.to_s
    id = username
    account = nil
    account = Account.first(:account => username, :password => password)

    if !account.nil?
      if account.allow_login
        if current_user.nil?
          session[:user] = account.account
        end
        return account.role
      else
        return -1
      end
    else
      return nil
    end
    
  end

  def int_partial(template, locals=nil)
    locals = locals.is_a?(Hash) ? locals : {template.to_sym =>         locals}
    template=('_' + template.to_s).to_sym
    erb(template, {:layout => false}, locals)
  end

  def append_note(type, note, email)
    
    if (type)
      if (type == 'eng')
         note = note + '<br/>若您翻譯時有任何疑問, 歡迎來信至 上傳該信之員工' + email
      elsif(type == 'chi')
         note = note + '<br/>[中翻英] 若您翻譯時有任何疑問, 歡迎來信至 上傳該信之員工' + email
      end
    end

    note
  end
end


get '/' do
  redirect '/volunteer'
end

get '/logout' do
  session[:user] = nil

  redirect '/login'
end

get '/login' do
  erb :login
end

post '/login' do
  email = params[:email]
  password = params[:password]

  @type = authenticate_account(email, password, 'admin')
  if (@type)
    if (@type == -1)
      @not_allowed_login = true
      erb :login
    elsif (@type == 'admin')
      redirect '/admin'
    elsif (@type == 'employee')
      redirect '/employee'
    elsif (@type == 'voulenteer')
      redirect '/volunteer'
    end
  else
    @fail = true
    erb :login
  end


end

# admin

get '/admin' do
  protected!
  name = params[:search_name]
  role = params[:search_role]
  if (role && (role == 'employee' || role == 'voulenteer') && name && name.strip != '')
    @accounts = Account.all(:role => role, :name => name)
  elsif (role && (role == 'employee' || role == 'voulenteer'))
    @accounts = Account.all(:role => role)
  elsif (name && name.strip != '')
    @accounts = Account.all(:name => name)
  else
    @accounts = Array.new
  end

  if (params[:account_exists])
    @account_exists = true
  end

  erb :admin_index
end

# get '/admin' do
#   protected!
#   @accounts = Array.new
#   
#   erb :admin_index
# end

get '/employee/country' do
  employee!
  @countries = Country.all
  @template = Template.first
  @url = get_upload_url()
  erb :admin_country_index
end

get '/admin/log' do
  protected!
  logger.info("due:00")
  start_date = params[:start_date]
  end_date = params[:end_date]
  @query_string = ''
  if (start_date && end_date)
    @logs = Array.new
    puts "s: " + start_date
    puts "e: " + end_date
    s_date = Date.strptime(start_date, DATE_FORMAT)
    e_date = Date.strptime(end_date, DATE_FORMAT)
    # Zoo.all(:opened_on => (s..e))  
    # @logs = VoulenteerLog.all(:return_date => (s_date..e_date))
    logs = VoulenteerLog.all
    logs.each do |log|
      if (log.return_date >= s_date && log.return_date <= e_date)
        @logs.push(log)
      end
    end
    params.each do |key, value|
      @query_string += ("#{key}\=#{value}\&")
    end
  else
    #@logs = VoulenteerLog.all
    @logs = Array.new
  end

  @query_string2 = ''
  start_date2 = params[:start_date2]
  end_date2 = params[:end_date2]
  logger.info("due emeail::0 ")
  if (start_date2 && end_date2)
    s_date = Date.strptime(start_date2, DATE_FORMAT)
    e_date = Date.strptime(end_date2, DATE_FORMAT)
    @letters = Array.new
    letters = Letter.all(:due_date_3.not => nil)
    letters = letters.all(:return_date => nil, :due_date_3.lt => Date.today)
    # letters = letters.all(:return_file_url => nil, :status => '已領取')
    logger.info("due emeail::1 " + letters.size.to_s)
    letters.each do |letter|
      if (letter.claim_date && letter.claim_date >= s_date && letter.claim_date <= e_date)
        @letters.push(letter)
      end
    end
    logger.info("due emeail::2 " + letters.size.to_s)
    params.each do |key, value|
      @query_string2 += ("#{key}\=#{value}\&")
    end
  else
    #@letters = Letter.all(:due_date.not => nil)
    #@letters = @letters.all(:due_date.lt => Date.today)
    #@letters = @letters.all(:return_file_url => nil, :status => '已領取')
    @letters = Array.new
  end

  erb :admin_log_index
end

get '/admin/vou' do
  protected!
  start_date = params[:start_date]
  end_date = params[:end_date]
  @query_string = ''

  if (start_date && end_date)
    s_date = Date.strptime(start_date, DATE_FORMAT)
    e_date = Date.strptime(end_date, DATE_FORMAT)
    letters = Letter.all(:return_date.not => nil)
    @letters = Array.new

    letters.each do |letter|
      if (letter.return_date >= s_date && letter.return_date <= e_date)
        @letters.push(letter)
      end
      # @letters = @letters.all(:order => [:voulenteer_id.asc])
    end
    params.each do |key, value|
      @query_string += ("#{key}\=#{value}\&")
    end
  else
    #@letters = Letter.all(:return_date.not => nil)
    @letters = Array.new
    # @letters = @letters.all(:order => [:voulenteer_id.asc])
  end

  erb :admin_vou

end

get '/admin/vou_status' do
  protected!

 # get all volunteers
 all_volunteers = Account.all(:role => 'voulenteer')
 @in_service_volunteers = Array.new
 @available_volunteers = Array.new
 @letters = Hash.new
 # look up if any letter claimed by volunteer
 all_volunteers.each do |volunteer|
   all_claimed_letters = Letter.all(:claim_date.not =>nil, :voulenteer_account => volunteer.account, :return_date => nil, :order => [ :claim_date.desc ])
   if (all_claimed_letters.size == 0)
      # if not, put into available volunteers
      @available_volunteers.push(volunteer)
      last_returned_letter = Letter.first(:return_date.not => nil, :voulenteer_account => volunteer.account, :order => [ :return_date.desc ])
      @letters[volunteer.account] = last_returned_letter
   else
      # if yes, insert into its letters attribute
      @in_service_volunteers.push(volunteer)
      @letters[volunteer.account] = all_claimed_letters
   end
 end
   logger.info("in service: " + @in_service_volunteers.size.to_s)
   logger.info("not in service: " + @available_volunteers.size.to_s)
   erb :admin_vou_status
end

get '/admin/is_account_exist' do
  protected!

  logger.info("called")
  acc = Account.first(:account => params[:account])
  acc.nil? ? "no" : "yes"
end

post '/create_account' do
  protected!

  acc = Account.first(:account => params[:account])
  logger.info("acc:" + acc.to_s)
  @account_exists = nil
  if (acc.nil?)
    account = Account.create(:account=>params[:account].strip, :password=>params[:password].strip, :role=>params[:role],
                             :name=>params[:name].strip, :voulenteer_id=>params[:voulenteer_id].strip, :voulenteer_type=>params[:voulenteer_type], :jobs=>0)
  else
    @account_exists=true
  end
  logger.info("result:" + @account_exists.to_s)
  if @account_exists
    redirect '/admin?account_exists=' + @account_exists.to_s
  else
    redirect '/admin'
  end
end

post '/employee/create_country' do
  employee!
  if (params[:name] && params[:continent])
    country = Country.create(:name=>params[:name], :continent=>params[:continent])
  end
  redirect '/employee/country'
end

post '/employee/search_country' do
  employee!
  @countries = Country.all(:name => params[:country])
  @url = get_upload_url()

  erb :admin_country_index
end

post '/employee/delete_country' do
  employee!
  id = params[:id]
  if (id)
    country = Country.get(id)
    if (!country.nil?)
      country.destroy
    end
  end
  redirect '/employee/country'
end

post '/delete_account' do
  protected!
  id = params[:id]
  if (id)
    account = Account.get(id)
    if (!account.nil?)
      account.destroy
    end
  end
  redirect '/admin'
end


post '/update_account' do
  protected!
  id = params[:id]
  if (!id.nil?)
    account = Account.get(id)
    puts "debug" + account.to_s
    if !params[:role].nil? && params[:role]!='please select'
      puts "debug: save: role"
      account.role = params[:role]
    end
    if !params[:name].nil?
      puts "debug: save: name"
      account.name = params[:name]
    end
    if !params[:voulenteer_id].nil?
      puts "debug: save: voulenteer_id"
      account.voulenteer_id = params[:voulenteer_id]
    end
    if !params[:voulenteer_type].nil? && params[:voulenteer_type]!='please select'
      puts "debug: save: voulenteer_type"
      account.voulenteer_type = params[:voulenteer_type]
    end
    if params[:email]
      account.email = params[:email]
    end
    if params[:weekly_email]
      account.weekly_email = params[:weekly_email]
    end
    if params[:allow_login]
      account.allow_login = params[:allow_login]
    end
    if params[:note]
      account.note = params[:note]
    end
    account.save
  end
  redirect '/admin'
end
# end


# employee
PAGESIZE=10
DATE_FORMAT='%m/%d/%Y'
get '/employee' do
  employee!
  @url = get_upload_url()
  trans_type = params[:type]
  @trans_type = trans_type.nil? ? 'eng' : trans_type
  params[:type] = @trans_type

  # pagaing
  bookmark = params[:start]
  offset = bookmark.nil? ? 0 : get_offset(bookmark)
  @account = current_user
  letters = get_letters
  @letters = Array.new
  @return_letters = Array.new
  
  if (current_user[:account] == 'srdvs@worldvision.org.tw')
    @letters = letters.all(:trans_type=> @trans_type, :status.in => ['unclaimed', 'emergent', 'claimed'], :order => [ :create_date.desc ])
    @return_letters = letters.all(:trans_type=> @trans_type, :status => 'returned', :order => [ :return_date.desc ])
  else
    @letters = letters.all(:employee_id=> current_user[:account].to_s, :status.in => ['unclaimed', 'emergent', 'claimed'], :trans_type=> @trans_type, :order => [ :create_date.desc ])
    @return_letters = letters.all(:employee_id=> current_user[:account].to_s, :status => 'returned', :trans_type=> @trans_type, :order => [ :return_date.desc ])
  end

# @todo investigate why count does not work
  @count = @letters.size
  @return_letters_count = @return_letters.size
  logger.info("count:" + @count.to_s)
  logger.info("r_count:" + @return_letters_count.to_s)

  if (@letters.size > PAGESIZE)
    @letters = @letters[offset, PAGESIZE]
  end
  if (@return_letters.size > PAGESIZE)
    @return_letters = @return_letters[offset, PAGESIZE]
  end
  # other fields
  @countries = Country.all
  @employees = Account.all(:role => 'employee')


  if (request.query_string.nil?)
    @query_string = nil
  else
    if (request.query_string.index('\?').nil?)
      @query_string = request.query_string
    else
      @query_string = request.query_string[0, request.query_string.index('\?')+1]
    end
  end
  @query_string = ''
  # puts "query_string: " + @query_string
  puts "criteria: " + @criteria

  params.each do |key, value|
    if ((key != 'sort' && key != 'start') && value != '請選擇')
      puts "Param #{key}\=#{value}"
      @query_string += ("#{key}\=#{value}\&")
    end
  end

  @query_string2 = ''
  params.each do |key, value|
    if (key != 'l_type' && (key != 'country') && (key != 'note') && (key != 'number_of_letters') && value != '請選擇')
      puts "Param #{key}\=#{value}"
      @query_string2 += ("#{key}\=#{value}\&")
    end
  end

  # paging
  total_page = (@count/PAGESIZE.to_f).ceil
  @pages = Array.new
  for i in (1..total_page)
    @pages.push(i)
  end

  total_page = (@return_letters_count/PAGESIZE.to_f).ceil
  @return_letters_pages = Array.new
  for i in (1..total_page)
    @return_letters_pages.push(i)
  end


  erb :employee_index
end

post '/update_letter' do
  # puts "update_letter"
  employee!
  id = params[:id]
  if (id)
    letter = Letter.get(id)
    if (params[:country])
      puts ("c_id" + params[:country])
      country = Country.first(:name=>params[:country])
      if (country)
        letter.country_name = country.name
        letter.country_id = country.id
      end
    end
    if (params[:note])
      puts ("note: " + params[:note])
      letter.note = params[:note]
    end
    if (params[:number_of_letters] && params[:number_of_letters] != '')
      puts ("letters: " + params[:number_of_letters])
      letter.number_of_letters = params[:number_of_letters]
    end
    if (params[:letter_source_type] && params[:letter_source_type] != '')
      puts ("letters: " + params[:letter_source_type])
      letter.letter_source_type = params[:letter_source_type]
    end
    if (params[:return_days] && params[:return_days] != '')
      puts ("letters: " + params[:return_days])
      letter.return_days = params[:return_days]
    end

    letter.show = 'true'
    if (letter.employee_id == current_user[:account].to_s)
      letter.save
    end
  end
  @query_string = ''
  params.each do |key, value|
    if ((key == 'sort' || key == 'start' || key == 'type' || key == 'country_id' || key=='field' || key == 'date' || key == 'employee_id' || key == 'letter_status' || key == 'start') && value != '請選擇')
      puts "Param #{key}\=#{value}"
      @query_string += ("#{key}\=#{value}\&")
    end
  end
  
  "success"
end

get '/delete_letter' do
  employee!
  ids = params[:ids]
  if (ids)
    ids.each do |id|
      logger.info("id:" + id.to_s)
      letter = Letter.get(id)
      if (!letter.nil? && letter.claim_date.nil? && letter.employee_id == current_user[:account].to_s)
          letter.deleted = 1
          letter.show = 'false'
          letter.save
      end
    end
  end
  redirect '/employee'
end
# end_date

# voulenteer
get '/volunteer' do
  voulenteer!

  if (session[:has_been_claimed])
    @letter_has_been_claimed = true
    session[:has_been_claimed] = nil
  end

  @url = get_upload_url()
  @account = current_user
  _trans_type = params[:type]
  @trans_type = nil
  if ((_trans_type != nil) && (_trans_type == 'chi' || _trans_type == 'eng'))
    @trans_type = _trans_type
  end

  @trans_type = @trans_type.nil? ? @account.voulenteer_type.nil? ? 'eng' : @account.voulenteer_type == 'both' ? 'chi' : @account.voulenteer_type[0, 3] : @trans_type
  @account_trans_type = @account.voulenteer_type.nil? ? 'both' : @account.voulenteer_type
  puts "===> trans_type" + @trans_type
  if (@trans_type == 'both')
    @all_letters = Letter.all(:deleted => false, :due_date => nil, :show=>'true', :order=>[:create_date.asc])
  else
    @all_letters = Letter.all(:deleted => false, :due_date => nil, :show=>'true', :trans_type=>@trans_type, :order=>[:create_date.asc])
  end
  @letters = Array.new
  @emergent_letters = Array.new
  puts "@all_letters: " + @all_letters.size.to_s
  @all_letters.each do |letter|
    if (letter.show == 'true')
      if (letter.status =='emergent')
        @emergent_letters.push(letter)
      else
        @letters.push(letter)
      end
    end
  end

  @hand_writing_letters = Array.new
  @typing_letters = Array.new

  @letters.each do |letter|
    if (letter.letter_source_type =='手寫稿')
      @hand_writing_letters.push(letter)
    else
      @typing_letters.push(letter)
    end
  end

  @voulenteer_letters = Letter.all(:due_date.not => nil, :order => [ :due_date.desc ], :voulenteer_account => current_user[:account])
#  @voulenteer_letters = Array.new
#  voulenteer_id = current_user[:voulenteer_id]
#  @claim_letters.each do |letter|
#    if (letter.voulenteer_id == voulenteer_id)
#      @voulenteer_letters.push(letter)
#    end
#  end


  # paging
  @pages = get_paginator(@letters)
  @emergent_pages = get_paginator(@emergent_letters)
  @hand_writing_pages = get_paginator(@hand_writing_letters)
  @typing_pages = get_paginator(@typing_letters)

  bookmark = params[:start]
  offset = params[:start].nil? ? 0 : get_offset(params[:start])
  em_offset = params[:em_start].nil? ? 0 : get_offset(params[:em_start])
  hw_offset = params[:hw_start].nil? ? 0 : get_offset(params[:hw_start])
  ty_offset = params[:ty_start].nil? ? 0 : get_offset(params[:ty_start])

  if (@letters.size > PAGESIZE)
    @letters = @letters[offset, PAGESIZE]
  end

  logger.info("l_size" + @letters.size.to_s)

  if (@emergent_letters.size > PAGESIZE)
    @emergent_letters = @emergent_letters[em_offset, PAGESIZE]
  end

  logger.info("e_size" + @emergent_letters.size.to_s)

  if (@hand_writing_letters.size > PAGESIZE)
    @hand_writing_letters = @hand_writing_letters[hw_offset, PAGESIZE]
  end

  if (@typing_letters.size > PAGESIZE)
    @typing_letters = @typing_letters[ty_offset, PAGESIZE]
  end

  logger.info("offset" + offset.to_s)

  @account_id = current_user[:id]
  logger.info("account_id:" + @account_id.to_s)
  get_template
  
  @latest5news = News.all(:status => 'online', :order => [:created_date.desc], :limit => 5)
  if (@latest5news.length > 5)
    @latest5news = @latest5news[0, 4]
  end   

  erb :voulenteer_index
end

post '/re_upload' do
  voulenteer!

  id = params[:id]
  logger.info('id' + id.to_s)

  if id
    logger.info('save letter')
    letter = Letter.get(id)
    letter.re_upload = true
    letter.save
  end

  redirect '/volunteer'
end

get '/volunteer/template' do
  get_template
  @asia_countries = Country.all(:continent => '亞洲')
  @africa_countries = Country.all(:continent => '非洲')
  @mid_east_countries = Country.all(:continent => '中東/東歐')
  @latin_america_countries = Country.all(:continent => '拉丁美洲')
  erb :vou_template
end

get '/volunteer/note' do
  get_template
  @asia_countries = Country.all(:continent => '亞洲')
  @africa_countries = Country.all(:continent => '非洲')
  @mid_east_countries = Country.all(:continent => '中東/東歐')
  @latin_america_countries = Country.all(:continent => '拉丁美洲')
  erb :vou_note
end

get '/volunteer/noun' do
  get_template
  @asia_countries = Country.all(:continent => '亞洲')
  @africa_countries = Country.all(:continent => '非洲')
  @mid_east_countries = Country.all(:continent => '中東/東歐')
  @latin_america_countries = Country.all(:continent => '拉丁美洲')
  erb :vou_noun
end

get '/show_all_news' do
  get_template
  @news = News.all(:status => 'online', :order => [:created_date.desc])
    
  erb :vou_all_news
end

post '/claim_letter' do
  voulenteer!
  id = params[:id]
  if (!id.nil?)
    letter = Letter.get(id)
    if (letter.voulenteer_id == nil)
      letter.voulenteer_id = current_user[:voulenteer_id]
      letter.voulenteer_account = current_user[:account]
      letter.voulenteer_name = current_user[:name]
      now = Time.now   
      localtime = now + 28000 
      logger.info(localtime)
      today = Date.parse(localtime.strftime('%Y/%m/%d'))
      
#       
#      now = DateTime.parse(time.to_s)
      letter.claim_date = today
      
      # eng2chi letters
      if (letter.return_days.nil?)
        letter.due_date = today + 7
        letter.due_date_3 = today + 10
      #if return days, chi2eng letters 
      else
        return_days = (letter.return_days - 1)
        letter.due_date = today + return_days
        letter.due_date_3 = today + return_days + 3
      end
      letter.status="claimed"
      letter.save
      if (current_user[:email] != nil)
        # uri = URI.parse("http://www.worldvision-tw.appspot.com/queue_email?mailId=1&email=" + current_user[:email] + "&id=" + id.to_s)
        # http = Net::HTTP.new(uri.host, uri.port)
        #       http.read_timeout = 30
        #       request = Net::HTTP::Get.new(uri.request_uri)
        #
        # http.request(request)
        fetcher = URLFetchServiceFactory.getURLFetchService
        url_for_vou = URL.new("http://www.worldvision-tw.appspot.com/queue_email?mailId=1&email=" + current_user[:email] + "&id=" + id.to_s)
        url_for_emp = URL.new("http://www.worldvision-tw.appspot.com/queue_email?mailId=6&email=" + letter.employee_id + "&id=" + id.to_s)
        fetcher.fetchAsync(url_for_vou)
        fetcher.fetchAsync(url_for_emp)
        # url = URI.parse("http://www.worldvision-tw.appspot.com/queue_email?mailId=1&email=" + current_user[:email] + "&id=" + id.to_s)
        # AppEngine::URLFetch.fetchAsync(url)
      end

      @hash_key = letter.upload_file_url
    else
      session[:has_been_claimed] = true
    end
  end
  if  @hash_key
    redirect '/volunteer?hash_key=' + @hash_key
  else
    redirect '/volunteer'
  end

end

get '/send_thank_you_email' do
  id = nil
  id = params[:letterId]
  puts "id: " + id.to_s
  if (!id.nil?)
    letter = Letter.get(id)
    puts "letter " + letter.to_s
    puts "vou_id " + letter.voulenteer_id
    vou = Account.first(:voulenteer_id => letter.voulenteer_id)
    puts "vou_email " + vou.email
    uri = URI.parse("http://www.worldvision-tw.appspot.com/queue_email?mailId=2&email=" + vou.email + "&id=" + id.to_s)
    Net::HTTP.get_response(uri)

    puts "emp_id" + letter.employee_id
    emp = Account.first(:account => letter.employee_id)
    puts "emp_email " + emp.email
    uri2 = URI.parse("http://www.worldvision-tw.appspot.com/queue_email?mailId=3&email=" + emp.email + "&id=" + id.to_s)
    Net::HTTP.get_response(uri2)
  end
  erb :nothing
end

post '/return_letter' do
  voulenteer!
  id = params[:id]
  if (!id.nil?)
    letter = Letter.get(id)
    claim_date = letter.claim_date
    letter.voulenteer_id = nil
    letter.voulenteer_name = nil
    letter.voulenteer_account = nil
    letter.claim_date = nil
    letter.due_date = nil
    letter.due_date_3 = nil
    voulenteer = Account.get(current_user[:id])
    jobs = voulenteer.jobs
    jobs -= 1
    voulenteer.jobs = jobs
    voulenteer.save
    letter.status="emergent"
    letter.save

    log = VoulenteerLog.new
    log.voulenteer_id = current_user[:account]
    log.voulenteer_name = current_user[:name]
    log.return_date = Date.today
    log.claim_date = claim_date
    log.letter_id = id
    if (!params[:excuse].nil?)
      log.excuse =params[:excuse]
      puts params[:excuse]
    end
    log.save

    fetcher = URLFetchServiceFactory.getURLFetchService
    url_for_emp = URL.new("http://www.worldvision-tw.appspot.com/queue_email?mailId=8&email=" + letter.employee_id + "&id=" + id.to_s + "&volunteerId=" + current_user[:voulenteer_id])
    fetcher.fetchAsync(url_for_emp)
  end
  redirect '/volunteer'
end

post '/create_news' do
  protected!
  
  news = News.new
  news.title = params[:title]
  news.content = params[:content]
  news.status = "offline"
  news.save
  
  redirect '/admin_news_index'
end

post '/update_news' do
  protected!
  
  id = params[:id]
  
  if (id)
    news = News.get(id)
    if (params[:title])
      news.title = params[:title]
    end
    if (params[:content])
      news.content = params[:content]
    end
    if (params[:status])
      if (news.created_date == nil)
          news.created_date = Date.today
      end
      news.status = params[:status]
    end
    news.save
  end
  
  redirect '/admin_news_index'
end

post '/delete_news' do
  protected!
  
  id = params[:id]
    
  if (id)
    news = News.get(id)
    
    news.destroy
  end
  
  redirect '/admin_news_index'
end

get '/admin_news_index' do
  protected!
  
  @news = News.all
  
  erb :admin_news_index
end

get '/preview_news' do
    
  id = params[:id]
  @news = nil  
  if (id)
    news = News.get(id)
    @news = news
  end
  
  erb :admin_news_preview
end

get '/migrate' do
  letters = Letter.all(:return_days => nil)
  letters.each do |letter|
    if (letter.return_days == nil)
      letter.return_days = 0
      letter.save
    end
  end
  redirect '/admin'
end

get '/is_weekly_email' do
  @account = Account.first(:account => params[:email])
  
  erb :test
end

post '/test2' do
  db = params[:db]
  if (db)
    db.each do |d|
      logger.info("db: " + d.to_s)
    end

  end
  erb :test
end

$blobstoreService = com.google.appengine.api.blobstore.BlobstoreServiceFactory.getBlobstoreService();

def get_upload_url()
  $blobstoreService.createUploadUrl("/upload")
end

def get_letters()
  # search
  trans_type = params[:type]
  trans_type = params[:type].nil? ? nil : params[:type] == '' ? nil : params[:type]
  country_id = params[:country_id].nil? ? nil : params[:country_id] == '' ? nil : params[:country_id].to_i
  employee_id = params[:employee_id].nil? ? nil : params[:employee_id]=='' ? nil : params[:employee_id]
  date = params[:date].nil? ? nil : params[:date]=='' ? nil : params[:date]
  status = params[:letter_status].nil? ? nil : params[:letter_status] == '' ? nil : params[:letter_status]

  # sorting
  sort = params[:sort]
  field = params[:field]


  @criteria = ''
  @letters = Letter.all(:deleted => 0)
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
  if (status)
    @letters = @letters.all(:status => status)
  end
  if (sort)
    if (field)
      if (sort == 'asc')
        @letters = @letters.all(:order=>[:employee_id.asc])
      elsif (sort == 'desc')
        @letters = @letters.all(:order=>[:employee_id.desc])
      end
      @criteria += ('sort='+sort+'&filed='+field)
    else
      if (sort == 'asc')
        @letters = @letters.all(:order=>[:create_date.asc])
      elsif (sort == 'desc')
        @letters = @letters.all(:order=>[:create_date.desc])
      end
      @criteria += ('sort='+sort)
    end
  else
    @letters = @letters.all(:order=>[:create_date.desc])
  end
  @letters
end

def get_paginator(letters)
  count = letters.size
  puts "count" + count.to_s
  total_page = (count/PAGESIZE.to_f).ceil
  puts "total_page" + total_page.to_s
  pages = Array.new
  for i in (1..total_page)
    pages.push(i)
  end
  pages == 1 ? Array.new : pages
end

def get_template
  @template = Template.first
end

def get_offset(bookmark)
  offset = (bookmark.to_i-1)*PAGESIZE
  # if (bookmark.to_i > 1)
  #  offset = offset + 1
  # end

  offset
end

# end

