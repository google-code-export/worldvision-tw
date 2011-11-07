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
  property :letter_source_type, String
  property :priority, Integer
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
  property :deleted, Boolean, :default => 0

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

  def logger
    request.logger
  end

  def truncate (string)
    index = string.rindex('\\')
    if (index && index > 0)
      string = string[index+1, string.length]
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

  def int_partial(template, locals=nil)
    locals = locals.is_a?(Hash) ? locals : {template.to_sym =>         locals}
    template=('_' + template.to_s).to_sym
    erb(template, {:layout => false}, locals)
  end
end


get '/' do
  redirect '/voulenteer'
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


  erb :admin_index
end

# get '/admin' do
#   protected!
#   @accounts = Array.new
#   
#   erb :admin_index
# end

get '/admin/country' do
  protected!
  @countries = Country.all
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
    letters = letters.all(:due_date_3.lt => Date.today)
    # letters = letters.all(:return_file_url => nil, :status => '已領取')
    logger.info("due emeail::1 " + letters.size.to_s)
    letters.each do |letter|
      if (letter.claim_date >= s_date && letter.claim_date <= e_date)
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

post '/create_account' do
  protected!
  account = Account.create(:account=>params[:account], :password=>params[:password], :role=>params[:role],
                           :name=>params[:name], :voulenteer_id=>params[:voulenteer_id], :voulenteer_type=>params[:voulenteer_type], :email=>params[:email], :jobs=>0)
  redirect '/admin'
end

post '/admin/create_country' do
  protected!
  country = Country.create(:name=>params[:name])
  redirect '/admin/country'
end

post '/admin/delete_country' do
  protected!
  id = params[:id]
  if (id)
    country = Country.get(id)
    if (!country.nil?)
      country.destroy
    end
  end
  redirect '/admin/country'
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
  # pagaing
  bookmark = params[:start]
  offset = bookmark.nil? ? 0 : bookmark.to_i == 1 ? 0 : ((bookmark.to_i-1)*PAGESIZE)
  @account = current_user
  letters = get_letters
  paginal_letters = letters.all(:offset=> offset, :limit => PAGESIZE, :employee_id=> current_user[:account].to_s)
  @letters = Array.new
  @return_letters = Array.new
  paginal_letters.each do |letter|
    if (letter.status == '已譯返')
      @return_letters.push(letter)
    else
      @letters.push(letter)
    end
  end
  @count = @letters.size
  @return_letters_count = @return_letters.size
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

  trans_type = params[:type]
  @trans_type = trans_type.nil?? 'eng' : trans_type

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
      letter.country_id = params[:country]
      country = Country.first(:id=>params[:country])
      if (country)
        letter.country_name = country.name
      end
    end
    if (params[:l_type])
      puts ("l_type" + params[:l_type])
      letter.type = params[:l_type]
    end
    if (params[:note])
      puts ("note: " + params[:note])
      letter.note = params[:note]
    end
    if (params[:number_of_letters] && params[:number_of_letters] != '')
      puts ("letters: " + params[:number_of_letters])
      letter.number_of_letters = params[:number_of_letters]
    end
    letter.show = 'true'
    if (letter.employee_id == current_user[:account].to_s)
      letter.save
    end
  end
  @query_string = ''
  params.each do |key, value|
    if ((key == 'sort' || key == 'start' || key == 'type' || key == 'country_id' || key=='field' || key == 'date' || key == 'employee_id') && value != '請選擇')
      puts "Param #{key}\=#{value}"
      @query_string += ("#{key}\=#{value}\&")
    end
  end
  redirect '/employee?' + @query_string
end

post '/delete_letter' do
  employee!
  id = params[:id]
  if (!id.nil?)
    letter = Letter.get(id)
    if (!letter.nil?)
      if (letter.employee_id == current_user[:account].to_s)
        letter.deleted = 1
        letter.show = 0
        letter.save
      end
    end
  end
  redirect '/employee'
end
# end_date

# voulenteer
get '/voulenteer' do
  voulenteer!

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
    @all_letters = Letter.all(:due_date => nil, :show=>'true', :order=>[:create_date.asc])
  else
    @all_letters = Letter.all(:due_date => nil, :show=>'true', :trans_type=>@trans_type, :order=>[:create_date.asc])
  end
  @letters = Array.new
  @emergent_letters = Array.new
  puts "@all_letters: " + @all_letters.size.to_s
  @all_letters.each do |letter|
    if (letter.show == 'true')
      if (letter.status =='緊急')
        @emergent_letters.push(letter)
      else
        @letters.push(letter)
      end
    end
  end
  @claim_letters = Letter.all(:due_date.not => nil)
  @voulenteer_letters = Array.new
  voulenteer_id = current_user[:voulenteer_id]
  @claim_letters.each do |letter|
    if (letter.voulenteer_id == voulenteer_id)
      @voulenteer_letters.push(letter)
    end
  end

  bookmark = params[:start]
  offset = bookmark.nil? ? 0 : bookmark.to_i == 1 ? 0 : ((bookmark.to_i-1)*PAGESIZE)
  # paging
  @pages = get_paginator(@letters, offset)
  @emergent_pages = get_paginator(@emergent_letters, offset)

  @account_id = current_user[:id]
  puts "@account_id: " + @account_id.to_s

  erb :voulenteer_index
end

get '/voulenteer/template' do
  @countries = Country.all
  erb :vou_template
end

get '/voulenteer/note' do
  @countries = Country.all
  erb :vou_note
end

get '/voulenteer/noun' do
  @countries = Country.all
  erb :vou_noun
end


post '/claim_letter' do
  voulenteer!
  id = params[:id]
  if (!id.nil?)
    letter = Letter.get(id)
    letter.voulenteer_id = current_user[:voulenteer_id]
    letter.voulenteer_name = current_user[:account]
    letter.claim_date = Date.today
    letter.due_date = Date.today + 6
    letter.due_date_3 = Date.today + 9
    letter.status="已領取"
    letter.save
    if (current_user[:email] != nil)
      # uri = URI.parse("http://www.worldvision-tw.appspot.com/queue_email?mailId=1&email=" + current_user[:email] + "&id=" + id.to_s)
      # http = Net::HTTP.new(uri.host, uri.port)
      #       http.read_timeout = 30
      #       request = Net::HTTP::Get.new(uri.request_uri)
      #       
      # http.request(request)
      fetcher = URLFetchServiceFactory.getURLFetchService
      url = URL.new("http://www.worldvision-tw.appspot.com/queue_email?mailId=1&email=" + current_user[:email] + "&id=" + id.to_s)
      fetcher.fetchAsync(url)
      # url = URI.parse("http://www.worldvision-tw.appspot.com/queue_email?mailId=1&email=" + current_user[:email] + "&id=" + id.to_s)
      # AppEngine::URLFetch.fetchAsync(url)
    end
  end
  redirect '/voulenteer'
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
    letter.claim_date = nil
    letter.due_date = nil
    letter.due_date_3 = nil
    voulenteer = Account.get(current_user[:id])
    jobs = voulenteer.jobs
    jobs -= 1
    voulenteer.jobs = jobs
    voulenteer.save
    letter.status="緊急"
    letter.save

    log = VoulenteerLog.new
    log.voulenteer_id = current_user[:voulenteer_id]
    log.voulenteer_name = current_user[:name]
    log.return_date = Date.today
    log.claim_date = claim_date
    log.letter_id = id
    if (!params[:excuse].nil?)
      log.excuse =params[:excuse]
      puts params[:excuse]
    end
    log.save
  end
  redirect '/voulenteer'
end

get '/migration' do
  letters = Letter.all
  letters.each do |letter|
    letter.deleted = false
    letter.save
  end
  redirect 'admin'
end

get '/dodolo' do
  url = request.url
  l_index = url.rindex('dodolo?')
  print "url: " + url
  if l_index != nil
    @target_url = url[l_index+7, url.length]
    print "url2: " + @target_url
  end
  if @target_url && @target_url.index('http') == nil
    @target_url = nil
  end
  erb :dodolo
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
    @letters = @letters.all(:order=>[:create_date.asc])
  end
  @letters
end

def get_paginator(letters, offset)
  count = letters.size
  puts "count" + count.to_s
  puts "index" + offset.to_s
  total_page = (count/PAGESIZE.to_f).ceil
  puts "total_page" + total_page.to_s
  pages = Array.new
  for i in (1..total_page)
    pages.push(i)
  end
  pages == 1 ? Array.new : pages
end

# end

